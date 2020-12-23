package com.ikkiking.service;

import com.ikkiking.api.response.TagResponse.Tag;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.repository.TagCustom;
import com.ikkiking.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public TagResponse getTag(String query) {
        List<Tag> tagList = new ArrayList<>();

        List<TagCustom> tagsResp = tagRepository.findAllByTags(query);
        if (tagsResp != null) {
            tagsResp.forEach(tagCustom -> {
                System.out.println(tagCustom.getWeight());
                tagList.add(new Tag(tagCustom.getName(), tagCustom.getWeight()));
            });
        }
        TagResponse tagResponse = new TagResponse(tagList);
        return tagResponse;
    }
}
