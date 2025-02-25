package com.cupid.qufit.domain.admin.service;

import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO;
import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO.Response;
import com.cupid.qufit.domain.admin.dto.AdminSignupApprovalDTO;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.repository.profiles.TypeProfilesRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.entity.MemberStatus;
import com.cupid.qufit.entity.TypeProfiles;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final TypeProfilesRepository typeProfilesRepository;

    /*
     * * 회원 상태를 지정하지 않았을 경우, 전체 회원 정보를 조회함 (관리자는 제외)
     * */
    @Override
    public Page<AdminMemberInfoDTO.Response> getAllMember(Pageable pageable) {
        Page<Member> members = memberRepository.findAllByRole(pageable, MemberRole.USER);
        return getResponses(members);
    }

    /*
     * * 회원 상태를 지정했을 경우, 회원 상태 (대기중, 탈퇴, 승인) 회원 정보를 조회함 (관리자는 제외)
     * */
    @Override
    public Page<AdminMemberInfoDTO.Response> getMemberByStatus(Pageable pageable, String status) {
        Page<Member> members = memberRepository.findAllByRoleAndStatus(pageable, MemberRole.USER,
                                                                       MemberStatus.valueOf(status));
        return getResponses(members);
    }

    /*
     * * 관리자용 회원 정보 조회 DTO로 반환함
     * */
    private Page<AdminMemberInfoDTO.Response> getResponses(Page<Member> members) {
        Page<AdminMemberInfoDTO.Response> adminMemberInfoDTOs = members.map(member -> {
            TypeProfiles typeProfiles = typeProfilesRepository.findByMemberId(member.getId())
                                                              .orElseThrow(() -> new MemberException(
                                                                      ErrorCode.TYPE_PROFILES_NOT_FOUND));

            MemberInfoDTO.Response memberInfo = MemberInfoDTO.Response.of(member, typeProfiles);

            Response response = Response.builder()
                                        .status(String.valueOf(member.getStatus()))
                                        .memberInfo(memberInfo)
                                        .build();
            return response;
        });
        return adminMemberInfoDTOs;
    }

    /*
     * * 관리자 회원가입 승인
     * */
    @Override
    public AdminMemberInfoDTO.Response approveMemberSingup(AdminSignupApprovalDTO.Request request) {
        Member member = memberRepository.findById(request.getMemberId())
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원이 대기 중 상태인지 확인
        if (member.getStatus() != MemberStatus.PENDING) {
            throw new MemberException(ErrorCode.MEMBER_STATUS_NOT_PENDING);
        }

        // 회원 상태 변경
        MemberStatus status;
        if (request.getIsApproved()) {
            status = MemberStatus.APPROVED;
        } else {
            status = MemberStatus.REJECTED;
        }
        member.updateStatus(status);
        Member updatedMember = memberRepository.save(member);

        // 이상형 프로필 조회
        TypeProfiles typeProfiles = typeProfilesRepository.findByMemberId(request.getMemberId())
                                                          .orElseThrow(() -> new MemberException(
                                                                  ErrorCode.TYPE_PROFILES_NOT_FOUND));

        return AdminMemberInfoDTO.Response.builder()
                                          .status(String.valueOf(updatedMember.getStatus()))
                                          .memberInfo(MemberInfoDTO.Response.of(updatedMember, typeProfiles))
                                          .build();
    }
}
