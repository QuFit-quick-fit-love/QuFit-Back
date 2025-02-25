package com.cupid.qufit.domain.member.repository.mappingTags;

import com.cupid.qufit.entity.MemberPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberPersonalityRepository extends JpaRepository<MemberPersonality, Long> {
    /*
    * MemberId에 해당하는 값 모두 삭제
    * */
    void deleteAllByMemberId(@Param("memberId") Long memberId);}
