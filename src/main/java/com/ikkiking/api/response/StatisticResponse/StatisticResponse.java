package com.ikkiking.api.response.StatisticResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticResponse {
    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;
}
