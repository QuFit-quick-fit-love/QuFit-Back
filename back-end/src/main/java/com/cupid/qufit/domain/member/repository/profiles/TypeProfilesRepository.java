package com.cupid.qufit.domain.member.repository.profiles;

import com.cupid.qufit.entity.TypeProfiles;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeProfilesRepository extends JpaRepository<TypeProfiles,Long> {

    Optional<TypeProfiles> findByMemberId(Long memberId);
}
