package com.cupid.qufit.global.utils.elasticsearch.service;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;

public interface ESVideoRoomService {
    ESVideoRoom save(com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom esVideoRoom);

    ESVideoRoom findById(Long id);

    Iterable<ESVideoRoom> findAll();

    void deleteById(Long id);

    void deleteAll();

}
