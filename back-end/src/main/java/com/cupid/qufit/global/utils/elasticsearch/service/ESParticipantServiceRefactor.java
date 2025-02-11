package com.cupid.qufit.global.utils.elasticsearch.service;

import com.cupid.qufit.global.utils.elasticsearch.dto.RecommendRoomDTO.Request;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ESParticipantServiceRefactor implements ESParticipantService {

    @Override
    public ESParticipant save(ESParticipant entity) {
        return null;
    }

    @Override
    public ESParticipant findById(Long id) {
        return null;
    }

    @Override
    public Iterable<ESParticipant> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Long> recommendRoom(int i, Request dummyRequest) throws IOException {
        return null;
    }

    @Override
    public void deleteAllByRoomId(String string) {

    }

}
