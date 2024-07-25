package com.cupid.qufit.global.utils.elasticsearch;


import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexerHelper {

    private final ElasticsearchClientManager elasticsearchClientManager;
    private final ObjectMapper objectMapper;

    // ! 테이블 생성해 주는 메소드
    public Boolean createIndex(String indexName, Map<String, Object> indexTemplate) throws IOException {
        RestClient restClient = elasticsearchClientManager.getRestClient(indexName);

        // 인덱스가 이미 존재하는지 확인
        Request existsRequest = new Request("HEAD", "/" + indexName);
        Response existsResponse = restClient.performRequest(existsRequest);
        if (existsResponse.getStatusLine().getStatusCode() == 200) {
            System.out.println("Index already exists: " + indexName);
            return false;
        }

        // 인덱스가 존재하지 않는 경우 인덱스 생성
        Request request = new Request(HttpPut.METHOD_NAME, "/" + indexName);
        if (!ObjectUtils.isEmpty(indexTemplate)) {
            String requestBody = jsonMapToString(indexTemplate);
            HttpEntity entity = new NStringEntity(requestBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }

        Response response = restClient.performRequest(request);
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
