package com.cupid.qufit.domain.admin.service;

import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO;
import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO.Response;
import com.cupid.qufit.domain.admin.dto.AdminSignupApprovalDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminService {

    Page<Response> getAllMember(Pageable pageable);

    Page<AdminMemberInfoDTO.Response> getMemberByStatus(Pageable pageable, String status);

    Response approveMemberSingup(AdminSignupApprovalDTO.Request request);
}
