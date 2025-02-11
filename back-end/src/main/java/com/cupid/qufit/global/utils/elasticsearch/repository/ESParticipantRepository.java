package com.cupid.qufit.global.utils.elasticsearch.repository;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

//TODO: Elastic Search 살리기: 주석 살리기,  void deleteAllByVideoRoomId(String roomId); 추가
//public interface ESParticipantRepository extends ElasticsearchRepository<ESParticipant, String> {
public interface ESParticipantRepository extends JpaRepository<ESParticipant, String> ,ESParticipantRepositoryCustom{
}
