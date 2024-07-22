package com.cupid.qufit.global.utils.elasticsearch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexerHelper {

    private final ElasticsearchClientManager elasticsearchClientManager;
    private final ObjectMapper objectMapper;

    // TODO: 추후 방법1을 선택할 것인지 방법2를 선택할 것인지 정해야 함.
    // 테이블 생성해 주는 메소드. elastic에서 index가 테이블임
    public Boolean createIndex(String indexName, Map<String, Object> indexTemplate) throws IOException {
        // 방법1
        elasticsearchClientManager.getElasticsearchClient(indexName)
                                  .indices()
                                  .create(c -> c.index(indexName));

        // 방법2
        RestClient restClient = elasticsearchClientManager.getRestClient(indexName);
        Request request = new Request(HttpPut.METHOD_NAME, "/" + indexName);

        if (ObjectUtils.isNotEmpty(indexTemplate)) {
            String requestBody = jsonMapToString(indexTemplate);
            HttpEntity entity = new NStringEntity(requestBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
        }

        Response response = restClient.performRequest(request);
        return true;
    }

    private String jsonMapToString(Map<String, Object> indexTemplate) throws JsonProcessingException {
        return objectMapper.writeValueAsString(indexTemplate);
    }

}
