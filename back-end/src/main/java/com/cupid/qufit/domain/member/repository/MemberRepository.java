package com.cupid.qufit.domain.member.repository;

import com.cupid.qufit.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    * * email로 회원 조회
     */
    Optional<Member> findByEmail(@Param("email") String email);

}
