package com.ikkiking.api.response.StatisticResponse;

public class MyStatisticResponse extends StatisticResponse{
    public MyStatisticResponse(int postsCount, int likesCount, int dislikesCount, int viewsCount, long firstPublication) {
        super(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }
}
