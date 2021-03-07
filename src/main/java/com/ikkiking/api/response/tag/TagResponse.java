package com.ikkiking.api.response.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TagResponse {
    private List<Tag> tags;
}
