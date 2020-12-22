package com.ikkiking.api.response.StatisticResponse;

public class AllStatisticResponse extends StatisticResponse{
    public AllStatisticResponse(long postsCount, long likesCount, long dislikesCount, long viewsCount, long firstPublication) {
        super(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }
}
