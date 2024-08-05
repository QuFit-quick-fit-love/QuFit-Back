package com.cupid.qufit.global.utils.elasticsearch.controller;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;
import com.cupid.qufit.global.utils.elasticsearch.service.ESVideoRoomServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
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

    private final ESVideoRoomServiceImpl esVideoRoomServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<ESVideoRoom> getVideoRoomById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(esVideoRoomServiceImpl.findById(id));
    }

    @GetMapping
    public ResponseEntity<Iterable<ESVideoRoom>> getAllVideoRooms() {
        return ResponseEntity.ok().body(esVideoRoomServiceImpl.findAll());
    }

    @PostMapping
    public ResponseEntity<ESVideoRoom> createVideoRoom(@RequestBody @Valid ESVideoRoom esVideoRoom) {
        ESVideoRoom createdVideoRoom = esVideoRoomServiceImpl.save(esVideoRoom);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(createdVideoRoom.getId())
                                             .toUri();
        return ResponseEntity.created(uri).body(createdVideoRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoRoomById(@PathVariable("id") Long id) {
        esVideoRoomServiceImpl.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void deleteAllVideoRooms() {
        esVideoRoomServiceImpl.deleteAll();
    }


}
