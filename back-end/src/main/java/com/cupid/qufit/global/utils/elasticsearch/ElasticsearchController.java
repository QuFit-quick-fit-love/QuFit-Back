package com.cupid.qufit.global.utils.elasticsearch;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qufit/es")
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    // TODO: response dto 만들어서 리턴해주기
    @GetMapping("/create-index")
    public Boolean createIndex(@RequestParam String indexName) throws IOException {
        return elasticsearchService.createIndex(indexName);
    }

    @GetMapping("/{indexName}")
    public ResponseEntity<String> getIndex(@PathVariable String indexName) throws IOException {
        String indexJson = elasticsearchService.getIndex(indexName);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(indexJson, headers, HttpStatus.OK);
    }
}
