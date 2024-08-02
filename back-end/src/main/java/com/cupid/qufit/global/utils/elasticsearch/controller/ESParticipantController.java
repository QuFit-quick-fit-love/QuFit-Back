package com.cupid.qufit.global.utils.elasticsearch.controller;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;
import com.cupid.qufit.global.utils.elasticsearch.service.ESParticipantService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/qufit/es-participant")
@RequiredArgsConstructor
public class ESParticipantController {

    private final ESParticipantService esParticipantService;

    @GetMapping("/{id}")
    public ResponseEntity<ESParticipant> getVideoRoomById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(esParticipantService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Iterable<ESParticipant>> getAllVideoRooms() {
        return ResponseEntity.ok().body(esParticipantService.findAll());
    }

    @PostMapping
    public ResponseEntity<ESParticipant> createVideoRoom(@RequestBody @Valid ESParticipant esParticipant) {
        ESParticipant createdParticipant = esParticipantService.save(esParticipant);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(createdParticipant.getId())
                                             .toUri();
        return ResponseEntity.created(uri).body(createdParticipant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoRoomById(@PathVariable("id") Long id) {
        esParticipantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void deleteAllVideoRooms() {
        esParticipantService.deleteAll();
    }


}
