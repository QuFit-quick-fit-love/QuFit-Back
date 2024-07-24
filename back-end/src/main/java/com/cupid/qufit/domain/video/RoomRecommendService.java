package com.cupid.qufit.domain.video;

import com.cupid.qufit.global.utils.elasticsearch.ElasticsearchFileUtil;
import com.cupid.qufit.global.utils.elasticsearch.IndexerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomRecommendService {
    private final IndexerHelper indexerHelper;

    public Boolean createIndex(String indexName) throws IOException {
        Map<String, Object> indexTemplate = new ElasticsearchFileUtil().getFileContent("/index/room_recommend_template.json");
        return indexerHelper.createIndex(indexName, indexTemplate);
    }

    
}
