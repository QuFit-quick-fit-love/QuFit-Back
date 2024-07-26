package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO.Response;
import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.TypeProfiles;
import java.util.Map;

public interface MemberService {

    void saveMemberProfiles(Member member, MemberSignupDTO.Request requestDTO);

    TypeProfiles createTypeProfiles(Member member, MemberSignupDTO.Request requestDTO);

    void saveTypeProfilesInfo(TypeProfiles typeProfiles, MemberSignupDTO.Request requestDTO);

    Map<String, Response> signIn(MemberDetails memberDetails);
}
