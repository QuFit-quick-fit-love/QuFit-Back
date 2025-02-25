package com.cupid.qufit.global.utils.elasticsearch.service;

import com.cupid.qufit.global.utils.elasticsearch.dto.RecommendRoomDTO.Request;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import java.io.IOException;
import java.util.List;

public interface ESParticipantService {
    ESParticipant save(com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant entity);

    ESParticipant findById(Long id);

    Iterable<ESParticipant> findAll();

    void deleteById(Long id);

    void deleteAll();

    List<Long> recommendRoom(int i, Request dummyRequest) throws IOException;

    void deleteAllByRoomId(String string);
}
