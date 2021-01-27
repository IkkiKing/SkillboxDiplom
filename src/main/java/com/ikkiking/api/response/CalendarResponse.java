package com.ikkiking.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse {
    private Set<Integer> years;
    private Map<String, Long> posts;

}
