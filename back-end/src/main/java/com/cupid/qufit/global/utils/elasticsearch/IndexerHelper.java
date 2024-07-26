package com.cupid.qufit.global.utils.elasticsearch;


import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ESIndexException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexerHelper {

    private final ElasticsearchClientManager elasticsearchClientManager;
    private final ObjectMapper objectMapper;

    // 테이블의 상세 정보 조회
    public HttpEntity getIndexDetail(String indexName) throws IOException {
        RestClient restClient = elasticsearchClientManager.getRestClient(indexName);

        try {
            Request request = new Request("GET", "/" + indexName);
            HttpEntity entity = restClient.performRequest(request).getEntity();

            return entity;
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                throw new ESIndexException(ErrorCode.INDEX_NOT_FOUND);
            } else {
                throw new RuntimeException("알수없는 오류. backend log참조 필요");
            }
        }
    }

    // ! 테이블 생성해 주는 메소드
    public Boolean createIndex(String indexName, Map<String, Object> indexTemplate) throws IOException {
        RestClient restClient = elasticsearchClientManager.getRestClient(indexName);

        // 인덱스가 이미 존재하는지 확인
        Request existsRequest = new Request("HEAD", "/" + indexName);
        Response existsResponse = restClient.performRequest(existsRequest);

        if (existsResponse.getStatusLine().getStatusCode() == 200) {
            log.error(ErrorCode.INDEX_ALREADY_EXISTS.getMessage());
            throw new ESIndexException(ErrorCode.INDEX_ALREADY_EXISTS);
        }

        // 인덱스가 존재하지 않는 경우 인덱스 생성
        Request request = new Request(HttpPut.METHOD_NAME, "/" + indexName);
        if (!ObjectUtils.isEmpty(indexTemplate)) {
            String requestBody = jsonMapToString(indexTemplate);
            HttpEntity entity = new NStringEntity(requestBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }

        restClient.performRequest(request);

        return true;
    }

    public Boolean deleteIndex(String indexName) throws IOException {

        DeleteIndexRequest deleteIndexRequest = DeleteIndexRequest.of(d -> d.index(indexName));

        elasticsearchClientManager.getElasticsearchClient(indexName)
                                  .indices()
                                  .delete(deleteIndexRequest);

        return true;
    }

    // ! index안에 있는 document의 수 세기
    // * 페이징이나 결과값 확인할때 사용
    public Long countIndex(String indexName) throws IOException {
        CountRequest countRequest = CountRequest.of(c -> c.index(indexName));
        return elasticsearchClientManager.getElasticsearchClient(indexName).count(countRequest).count();
    }

    // ! json형태의 template을 String으로 바꿔주는 메소드
    private String jsonMapToString(Map<String, Object> indexTemplate) throws JsonProcessingException {
        return objectMapper.writeValueAsString(indexTemplate);
    }
}
