package com.cupid.qufit.domain.member.repository.profiles;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.entity.MemberStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
     * * email로 회원 조회
     */
    Optional<Member> findByEmail(String email);

    /*
     * * 닉네임 존재 유무 확인
     * */
    Boolean existsByNickname(String nickname);

    /*
     * * 멤버 조회
     * */
    @EntityGraph(attributePaths = {"location", "memberHobbies", "memberPersonalities", "MBTI"})
    Optional<Member> findById(Long id);

    /*
     * * 멤버 역할에 따라 멤버 조회
     * */
    @EntityGraph(attributePaths = {"location", "memberHobbies", "memberPersonalities", "MBTI"})
    Page<Member> findAllByRole(Pageable pageable, MemberRole role);

    /*
     * * 멤버 역할과 멤버 상태에 따라 멤버 조회
     * */
    @EntityGraph(attributePaths = {"location", "memberHobbies", "memberPersonalities", "MBTI"})
    Page<Member> findAllByRoleAndStatus(Pageable pageable, MemberRole role, MemberStatus Status);
}
