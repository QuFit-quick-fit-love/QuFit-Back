package com.cupid.qufit.global.utils.elasticsearch.service;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;

public interface ESParticipantService {
    ESParticipant save(com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant entity);

    ESParticipant findById(Long id);

    Iterable<ESParticipant> findAll();

    void deleteById(Long id);

    void deleteAll();
}
