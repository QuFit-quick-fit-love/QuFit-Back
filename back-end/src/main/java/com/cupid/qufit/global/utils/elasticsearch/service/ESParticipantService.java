package com.cupid.qufit.global.utils.elasticsearch.service;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ESParticipantException;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import com.cupid.qufit.global.utils.elasticsearch.repository.ESParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ESParticipantService implements ESService<ESParticipant> {

    private final ESParticipantRepository esParticipantRepository;

    @Override
    public ESParticipant save(ESParticipant entity) {
        return esParticipantRepository.save(entity);
    }

    @Override
    public ESParticipant findById(Long id) {
        return esParticipantRepository.findById(id).orElseThrow(()-> new ESParticipantException(ErrorCode.PARTICIPANT_NOT_FOUND));
    }

    @Override
    public Iterable<ESParticipant> findAll() {
        return esParticipantRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        esParticipantRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        esParticipantRepository.deleteAll();
    }
}
