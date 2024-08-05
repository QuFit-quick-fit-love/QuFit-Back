package com.cupid.qufit.global.utils.elasticsearch.repository;

import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESParticipantRepository extends ElasticsearchRepository<ESParticipant, Long> {

}
