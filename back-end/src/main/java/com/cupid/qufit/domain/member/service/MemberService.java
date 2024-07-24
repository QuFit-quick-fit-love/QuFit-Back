package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO;
import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.TypeProfiles;

public interface MemberService {

    void saveMemberProfiles(Member member, MemberSignupDTO.request requestDTO);

    TypeProfiles createTypeProfiles(Member member, MemberSignupDTO.request requestDTO);

    void saveTypeProfilesInfo(TypeProfiles typeProfiles, MemberSignupDTO.request requestDTO);

    MemberSigninDTO.response signIn(MemberDetails memberDetails);
}
