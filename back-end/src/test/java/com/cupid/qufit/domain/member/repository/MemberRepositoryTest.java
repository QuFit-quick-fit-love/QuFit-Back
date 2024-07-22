package com.cupid.qufit.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberStatus;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

@SpringBootTest
@Commit
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void 더미회원생성() {
        // 첫 번째 더미 회원 생성
        Member member1 = Member.builder()
                               .email("dummy1@example.com")
                               .password("password1")
                               .nickname("dummy1")
                               .createdAt(LocalDateTime.now())
                               .updatedAt(LocalDateTime.now())
                               .birthDate(LocalDate.of(1990, 1, 1))
                               .gender("M")
                               .bio("First dummy member")
                               .status(MemberStatus.PENDING)
                               .build();

        // 두 번째 더미 회원 생성
        Member member2 = Member.builder()
                               .email("dummy2@example.com")
                               .password("password2")
                               .nickname("dummy2")
                               .createdAt(LocalDateTime.now())
                               .updatedAt(LocalDateTime.now())
                               .birthDate(LocalDate.of(1992, 2, 2))
                               .gender("F")
                               .bio("Second dummy member")
                               .status(MemberStatus.PENDING)
                               .build();

        // 회원 저장
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 저장된 회원 확인
        assertThat(memberRepository.findById(member1.getId())).isPresent();
        assertThat(memberRepository.findById(member2.getId())).isPresent();
    }

}