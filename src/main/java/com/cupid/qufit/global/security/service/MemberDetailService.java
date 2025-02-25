package com.cupid.qufit.global.security.service;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
* *  ("/qufit/admin/login")로 요청된 관리자 로그인 요청을 처리함
* */
@Service
@RequiredArgsConstructor
@Log4j2
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member adminMember = memberRepository.findByEmail(username)
                                             .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("[admin login try] : " + username);

        // admin 회원인지 확인
        if (adminMember.getRole() == MemberRole.ADMIN) {
            log.info("[admin check success]");

            return MemberDetails.builder()
                                .id(adminMember.getId())
                                .email(adminMember.getEmail())
                                .pw(adminMember.getPassword())
                                .role(adminMember.getRole())
                                .build();
        }

        throw new MemberException(ErrorCode.NOT_ADMIN_MEMBER);
    }
}
