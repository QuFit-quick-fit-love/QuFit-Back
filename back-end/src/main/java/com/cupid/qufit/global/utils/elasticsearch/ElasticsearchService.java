package com.cupid.qufit.global.utils.elasticsearch;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final IndexerHelper indexerHelper;
    private final ElasticsearchClientManager elasticsearchClientManager;
    private final ObjectMapper objectMapper;

    // ! 인덱스 생성
    public Boolean createIndex(String indexName, String type) throws IOException {
        log.info("[createIndex] indexName: {}", indexName);
        Map<String, Object> indexTemplate;

        if (type.equals("p")) {
            indexTemplate = new ElasticsearchFileUtil().getFileContent("/index/participant_template.json");
        } else {
            indexTemplate = new ElasticsearchFileUtil().getFileContent("/index/video_room_template.json");
        }

        return indexerHelper.createIndex(indexName, indexTemplate);
    }

    // ! 인덱스 삭제
    public Boolean deleteIndex(String indexName) throws IOException {
        log.info("[deleteIndex] indexName: {}", indexName);
        return indexerHelper.deleteIndex(indexName);
    }

    // ! 인덱스 내 결과 수
    public Long countIndex(String indexName) throws IOException {
        log.info("[countIndex] indexName: {}", indexName);
        return indexerHelper.countIndex(indexName);
    }

    // ! 인덱스 정보 조회(mapping이나 setting설정 잘되어있는지 확인 or 잘 만들어졌는지)
    public String getIndex(String indexName) throws IOException {
        log.info("[getIndex] indexName: {}", indexName);
        return EntityUtils.toString(indexerHelper.getIndexDetail(indexName));
    }

    /**
     * ! 입력받은 json형태의 문서를 원하는 index(테이블)에 인덱싱
     *
     * @param indexName  : 저장하고 싶은 테이블의 이름
     * @param document   : 저장하고 싶은 데이터를 json형태로 받음
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
