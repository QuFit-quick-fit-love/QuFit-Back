package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.TypeProfiles;
import java.util.Map;

public interface MemberService {

    /*
     * * 회원 프로필 정보 저장
     * */
    void saveMemberProfiles(Member member, MemberInfoDTO.Request requestDTO);

    /*
     * * 이상형 프로필 생성
     * */
    TypeProfiles createTypeProfiles(Member member, MemberInfoDTO.Request requestDTO);

    /*
     * * 이상형 프로필 정보 저장
     * */
    void saveTypeProfilesInfo(TypeProfiles typeProfiles, MemberInfoDTO.Request requestDTO);

    Map<String, MemberSigninDTO.Response> signIn(MemberDetails memberDetails);

    /*
     * * 회원 정보 조회
     * */
    MemberInfoDTO.Response getMemberInfo(Long memberId);

    /*
     * * 회원 정보 수정
     * */
    MemberInfoDTO.Response updateMemberInfo(MemberInfoDTO.Request request, Long currentMemberId);

    /*
     * * 회원 탈퇴 설정
     * */
    Member deleteService(Long currentMemberId);

    /**
     * 이미지 생성 및 저장
     */
    void generateImage(MemberDetails memberDetails) throws Exception;
}
