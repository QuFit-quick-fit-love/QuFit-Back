package com.cupid.qufit.domain.member.repository.mappingTags;

import com.cupid.qufit.entity.TypeMBTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TypeMBTIRepository extends JpaRepository<TypeMBTI, Long> {
    /*
     * typeProfilesId 해당하는 값 모두 삭제
     * */
    void deleteAllByTypeProfilesId(@Param("typeProfilesId") Long typeProfilesId);
}
