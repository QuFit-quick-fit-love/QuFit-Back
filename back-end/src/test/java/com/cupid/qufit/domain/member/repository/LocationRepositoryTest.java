package com.cupid.qufit.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cupid.qufit.domain.member.repository.tag.LocationRepository;
import com.cupid.qufit.domain.member.repository.tag.TagRepository;
import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TagCateg;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void 더미지역생성(){

        Location location1 = Location.builder()
                                     .Si("Seoul")
                                     .build();
        Location location2 = Location.builder()
                                     .Si("Busan")
                                     .build();

        // 지역 저장
        locationRepository.save(location1);
        locationRepository.save(location2);

        // 저장 태그 확인
        assertThat(locationRepository.findById(location1.getId()).isPresent());
    }
}
