package com.ikkiking.service;

import com.ikkiking.api.response.tag.Tag;
import com.ikkiking.api.response.tag.TagResponse;
import com.ikkiking.base.exception.TagNotFoundException;
import com.ikkiking.repository.TagCustom;
import com.ikkiking.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TagService {
    private static final Double ROUND_DELIMETER = 100.0;
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Возвращает список тэгов блога.
     *
     * @param query строка с заданным тэгом
     * @return список найденных в БД тэгов
     */
    public ResponseEntity<TagResponse> tag(String query) {
        List<TagCustom> tagList = tagRepository.findAllByTags(query);

        TagCustom tagMax = tagList.stream()
                .max(Comparator.comparing(TagCustom::getPostsByTagCount)).orElseThrow(() ->
                        new TagNotFoundException("No tags in db"));

        Long countPosts = tagMax.getPostsCount();

        double k = 1.0 / ((double) tagMax.getPostsByTagCount() / (double) countPosts);

        List<Tag> tags = tagList.stream()
                .filter(f -> query == null || query.isEmpty() || f.getName().contains(query))
                .map(t -> new Tag(t.getName(),
                        getTagNormalizedWeight(k, t.getPostsByTagCount(), countPosts)))
                .collect(Collectors.toList());

        TagResponse tagResponse = new TagResponse(tags);
        return ResponseEntity.ok(tagResponse);
    }

    /**
     * Метод расчёта нормализованного веса тэга.
     * @param normCoefficient нормализующий коэффициент
     * @param postsByTagCount кол-во постов с конкретным тэгом
     * @param countPosts общее кол-во постов
     *
     * @return Нормализованный вес тэга
     * */
    private Double getTagNormalizedWeight(double normCoefficient,
                                          Long postsByTagCount,
                                          Long countPosts) {
        double result = ((double) postsByTagCount / (double) countPosts) * normCoefficient;
        return Math.round(result * ROUND_DELIMETER) / ROUND_DELIMETER;
    }
}
