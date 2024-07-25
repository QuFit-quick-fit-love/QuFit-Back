package com.cupid.qufit.domain.member.repository.profiles;

import com.cupid.qufit.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    * * email로 회원 조회
     */
    Optional<Member> findByEmail(String email);

    /*
    * * 닉네임 존재 유무 확인
    * */
    Boolean existsByNickname(String nickname);
}
