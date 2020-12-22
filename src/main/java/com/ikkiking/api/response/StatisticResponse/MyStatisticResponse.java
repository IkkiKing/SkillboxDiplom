package com.ikkiking.api.response.StatisticResponse;

public class MyStatisticResponse extends StatisticResponse{
    public MyStatisticResponse(long postsCount, long likesCount, long dislikesCount, long viewsCount, long firstPublication) {
        super(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }
}
