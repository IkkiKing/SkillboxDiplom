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


@Service
public class TagService {


    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public ResponseEntity<TagResponse> getTag(String query) {
        List<Tag> tagList = new ArrayList<>();

        List<TagCustom> tagsResp = tagRepository.findAllByTags(query);

        //Нужна ли проверка на null? Возможно репозиторий возвращает инициализированную коллекцию
        if (tagsResp != null) {
            tagsResp.forEach(tagCustom -> {
                tagList.add(new Tag(tagCustom.getName(), tagCustom.getWeight()));
            });
        }
        TagResponse tagResponse = new TagResponse(tagList);
        return ResponseEntity.ok(tagResponse);
    }
}
