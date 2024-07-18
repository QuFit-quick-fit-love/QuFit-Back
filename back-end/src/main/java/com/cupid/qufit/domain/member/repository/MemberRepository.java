package com.cupid.qufit.domain.member.repository;

import com.cupid.qufit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {


}
