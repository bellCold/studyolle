package com.studyolle.application;

import com.studyolle.domain.tag.Tag;
import com.studyolle.domain.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag == null) {
            tagRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }

}
