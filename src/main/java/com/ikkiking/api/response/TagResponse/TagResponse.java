package com.ikkiking.api.response.TagResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagResponse {
    private List<Tag> tags;
}
