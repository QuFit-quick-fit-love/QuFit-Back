package com.cupid.qufit.domain.video;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.cupid.qufit.global.utils.elasticsearch.ElasticsearchFileUtil;
import com.cupid.qufit.global.utils.elasticsearch.IndexerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomRecommendService {
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
}
