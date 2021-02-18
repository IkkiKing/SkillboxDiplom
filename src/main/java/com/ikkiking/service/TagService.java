package com.ikkiking.service;

import com.ikkiking.api.response.TagResponse.Tag;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.repository.TagCustom;
import com.ikkiking.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TagService {


    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public ResponseEntity<TagResponse> getTag(String query) {
        List<Tag> tagList = tagRepository.findAllByTags(query).stream()
                .map(t -> new Tag(t.getName(), t.getWeight()))
                .collect(Collectors.toList());

        TagResponse tagResponse = new TagResponse(tagList);

        return ResponseEntity.ok(tagResponse);
    }
}
