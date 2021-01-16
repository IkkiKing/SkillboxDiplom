package com.ikkiking.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class CalendarResponse {
    private Set<Integer> years;
    private Map<String, Long> posts;

}
