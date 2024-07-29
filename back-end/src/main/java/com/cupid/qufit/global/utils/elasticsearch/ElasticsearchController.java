package com.cupid.qufit.global.utils.elasticsearch;

import com.cupid.qufit.global.exception.exceptionType.ESIndexException;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Elasticsearch", description = "ES관련 기본 기능. 관리자 페이지에서 사용 필요.")
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    /**
     * 인덱스(테이블) 만들기
     * ! 관리자 페이지에서 사용하는 API입니다.
     *
     * @param indexName 내가 만들려는 인덱스(테이블)의 이름
     * @param type 내가 사용하려는 템플릿 종류("p" = participant, "v" = video_room)
     * @return 성공 코드
     * @throws IOException Elasticsearch와의 통신 문제
     */
    // TODO: 성공 response dto 만들어서 리턴해주기
    @GetMapping("/create-index")
    public Boolean createIndex(@RequestParam("index-name") String indexName, @RequestParam("type") String type) throws IOException {
        return elasticsearchService.createIndex(indexName, type);
    }

    /**
     * 인덱스의 설정 정보를 확인하는 api
     * ! 관리자 페이지에서 사용하는 API입니다.
     *
     * @param indexName 내가 조회하려는 인덱스의 이름
     * @return 인덱스 설정 정보. settings와 mappings 설정 확인
     * @throws IOException Elasticsearch와의 통신 문제
     * @throws ESIndexException 인덱스 조회 중 발생한 오류
     */
    @GetMapping("/{index-name}")
    public ResponseEntity<String> getIndex(@PathVariable("index-name") String indexName) throws IOException {
        String indexJson = elasticsearchService.getIndex(indexName);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(indexJson, headers, HttpStatus.OK);
    }
}
