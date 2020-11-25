package com.ikkiking.service;

import com.ikkiking.api.response.TagResponse.Tag;
import com.ikkiking.api.response.TagResponse.TagResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    public TagResponse getTagService(List<Tag> tags){
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("Java", 0.5));
        tagList.add(new Tag("Spring", 0.5));
        tagList.add(new Tag("Hibernate", 0.25));
        tagList.add(new Tag("Hadoop", 0.25));

        TagResponse tagResponse = new TagResponse(tagList);
        return tagResponse;
    }
}
