package com.cupid.qufit.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cupid.qufit.domain.member.repository.tag.TagRepository;
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
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Transactional
    void 더미태그생성(){
        List<Tag> dummyTags = new ArrayList<>();
        Tag personalityTag1 = Tag.builder()
                .tagCategory(TagCateg.PERSONALITY)
                .tagName("personality-tag-1")
                .build();
        dummyTags.add(personalityTag1);

        Tag personalityTag2 = Tag.builder()
                      .tagCategory(TagCateg.PERSONALITY)
                      .tagName("personality-tag-2")
                      .build();
        dummyTags.add(personalityTag2);

        Tag mbtiTag1 = Tag.builder()
                      .tagCategory(TagCateg.MBTI)
                      .tagName("mbti-tag-1")
                      .build();
        dummyTags.add(mbtiTag1);

        Tag mbtiTag2 = Tag.builder()
                          .tagCategory(TagCateg.MBTI)
                          .tagName("mbti-tag-2")
                          .build();
        dummyTags.add(mbtiTag2);

        Tag hobbyTag1 = Tag.builder()
                          .tagCategory(TagCateg.HOBBY)
                          .tagName("hobby-tag-1")
                          .build();
        dummyTags.add(hobbyTag1);

        Tag hobbyTag2 = Tag.builder()
                           .tagCategory(TagCateg.HOBBY)
                           .tagName("hobby-tag-2")
                           .build();
        dummyTags.add(hobbyTag2);

        // 태그 저장
        tagRepository.saveAll(dummyTags);

        // 저장 태그 확인
        assertThat(tagRepository.findById(hobbyTag1.getId()).isPresent());
    }
}
