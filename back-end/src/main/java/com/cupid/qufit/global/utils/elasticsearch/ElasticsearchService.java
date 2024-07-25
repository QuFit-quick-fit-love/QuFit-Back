package com.cupid.qufit.global.utils.elasticsearch;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final IndexerHelper indexerHelper;
    private final ElasticsearchClientManager elasticsearchClientManager;

    public Boolean createIndex(String indexName) throws IOException {
        log.info("[createIndex] indexName: {}", indexName);
        Map<String, Object> indexTemplate = new ElasticsearchFileUtil().getFileContent(
                "/index/room_recommend_template.json");
        return indexerHelper.createIndex(indexName, indexTemplate);
    }

    public Boolean deleteIndex(String indexName) throws IOException {
        log.info("[deleteIndex] indexName: {}", indexName);
        return indexerHelper.deleteIndex(indexName);
    }

    public Long countIndex(String indexName) throws IOException {
        log.info("[countIndex] indexName: {}", indexName);
        return indexerHelper.countIndex(indexName);
    }

    /** ! 입력받은 json형태의 문서를 원하는 index(테이블)에 인덱싱
     *
     * @param indexName : 저장하고 싶은 테이블의 이름
     * @param document : 저장하고 싶은 데이터를 json형태로 받음
     * @param documentId : 데이터의 고유 id값
     */
    public void indexDocument(String indexName, JsonData document, Long documentId) throws IOException {
        log.info("[addDocument] indexName: {}", indexName);
        IndexRequest<JsonData> indexRequest = IndexRequest.of(i -> i.index(indexName)
                                                                    .id(String.valueOf(documentId))
                                                                    .document(document));
        IndexResponse result = elasticsearchClientManager.getElasticsearchClient(indexName).index(indexRequest);
    }


}
