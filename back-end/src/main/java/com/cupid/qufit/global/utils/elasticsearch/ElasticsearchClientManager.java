package com.cupid.qufit.global.utils.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchClientManager {

    private final ElasticsearchFactory elasticsearchFactory;
    private final Map<String, ElasticsearchClient> clientMap = new ConcurrentHashMap<>();
    private final Map<String, RestClient> restClientMap = new ConcurrentHashMap<>();

    public ElasticsearchClient getElasticsearchClient(String indexName) {
        if (clientMap.containsKey(indexName)) {
            return clientMap.get(indexName);
        }
        ElasticsearchClient elasticsearchClient = elasticsearchFactory.getElasticsearchClient();
        clientMap.put(indexName, elasticsearchClient);
        return elasticsearchClient;
    }

    public RestClient getRestClient(String indexName) {
        if (restClientMap.containsKey(indexName)) {
            return restClientMap.get(indexName);
        }
        RestClient restClient = elasticsearchFactory.getRestClient();
        restClientMap.put(indexName, restClient);
        return restClient;
    }
}
