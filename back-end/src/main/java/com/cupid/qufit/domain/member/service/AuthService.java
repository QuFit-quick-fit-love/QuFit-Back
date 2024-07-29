package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.entity.Member;

public interface AuthService {

    MemberDetails kakaoLogin(String accessToken);

    MemberInfoDTO.Response signup(String accessToken, MemberInfoDTO.Request memberSignupRequestDTO);

    Boolean isNicknameDuplication(String nickname);

    /*
    * * member 객체를 PrincipalDetails DTO로 만드는 생성자
    * */
    default MemberDetails entityToMemberDetails(Member member) {
        return new MemberDetails(member.getId(), member.getEmail(), member.getRole());
    }
}
