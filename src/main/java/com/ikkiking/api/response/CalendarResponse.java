package com.ikkiking.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse {
    private List<Integer> years;
    private Map<String, Long> posts;

}
