package com.cupid.qufit.global.utils.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final IndexerHelper indexerHelper;

    public Boolean createIndex(String indexName) throws IOException {
        log.info("[createIndex] indexName: {}", indexName);
        Map<String, Object> indexTemplate = new ElasticsearchFileUtil().getFileContent("/index/room_recommend_template.json");
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


}
