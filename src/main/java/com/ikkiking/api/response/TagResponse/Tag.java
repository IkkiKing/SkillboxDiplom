package com.ikkiking.api.response.TagResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {
    private String name;
    private double weight;
}
