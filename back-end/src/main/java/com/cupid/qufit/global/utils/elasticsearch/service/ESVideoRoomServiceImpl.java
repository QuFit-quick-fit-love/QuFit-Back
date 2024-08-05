package com.cupid.qufit.global.utils.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ESVideoRoomException;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;
import com.cupid.qufit.global.utils.elasticsearch.repository.ESVideoRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ESVideoRoomServiceImpl {

    private final ESVideoRoomRepository esVideoRoomRepository;
    private final ElasticsearchClient elasticsearchClient;


    public ESVideoRoom save(ESVideoRoom esVideoRoom) {
        return esVideoRoomRepository.save(esVideoRoom);
    }

    public ESVideoRoom findById(Long id) {
        return esVideoRoomRepository.findById(id)
                                    .orElseThrow(() -> new ESVideoRoomException(ErrorCode.VIDEO_ROOM_NOT_FOUND));
    }


    public Iterable<ESVideoRoom> findAll() {
        return esVideoRoomRepository.findAll();
    }

    public void deleteById(Long id) {
        esVideoRoomRepository.deleteById(id);
    }

    public void deleteAll() {
        esVideoRoomRepository.deleteAll();
    }


}
