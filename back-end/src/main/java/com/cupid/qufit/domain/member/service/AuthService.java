package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.domain.member.dto.PrincipalDetails;
import com.cupid.qufit.entity.Member;

public interface AuthService {

    PrincipalDetails kakaoLogin(String accessToken);

    MemberSignupDTO.response signup(String accessToken, MemberSignupDTO.request memberSignupRequestDTO);

    /*
    * * member 객체를 PrincipalDetails DTO로 만드는 생성자
    * */
    default PrincipalDetails entityToPrincipalDetails(Member member) {
        return new PrincipalDetails(member);
    }
}
