package com.cupid.qufit.global.utils.elasticsearch.controller;

import com.cupid.qufit.global.utils.elasticsearch.dto.RecommendRoomDTO;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;
import com.cupid.qufit.global.utils.elasticsearch.service.ESVideoRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/qufit/es-video")
@RequiredArgsConstructor
@Tag(name = "elasticsearch", description = "Elasticsearch service테스트용 API")
@Slf4j
public class ESVideoRoomController {

    private final ESVideoRoomService esVideoRoomService;

    @GetMapping("/{id}")
    public ResponseEntity<ESVideoRoom> getVideoRoomById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(esVideoRoomService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Iterable<ESVideoRoom>> getAllVideoRooms() {
        return ResponseEntity.ok().body(esVideoRoomService.findAll());
    }

    @PostMapping
    public ResponseEntity<ESVideoRoom> createVideoRoom(@RequestBody @Valid ESVideoRoom esVideoRoom) {
        ESVideoRoom createdVideoRoom = esVideoRoomService.save(esVideoRoom);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(createdVideoRoom.getId())
                                             .toUri();
        return ResponseEntity.created(uri).body(createdVideoRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoRoomById(@PathVariable("id") Long id) {
        esVideoRoomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void deleteAllVideoRooms() {
        esVideoRoomService.deleteAll();
    }

    @GetMapping("/search")
    public void search() throws IOException {
        log.info("search");
        RecommendRoomDTO.Request dummyRequest = RecommendRoomDTO.Request.builder()
                                                                        .location("서울")
                                                                        .birthYear(1995)
                                                                        .typeAgeMax(35)
                                                                        .typeAgeMin(25)
                                                                        .MBTIs(List.of("INFJ", "INFP", "INTP"))
                                                                        .personalities(List.of("예술적", "이해심"))
                                                                        .hobbies(List.of("그림 그리기", "명상"))
                                                                        .gender("F")
                                                                        .build();
        esVideoRoomService.search(dummyRequest);
    }
}
