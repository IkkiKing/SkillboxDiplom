package com.ikkiking.api.response.StatisticResponse;

public class AllStatisticResponse extends StatisticResponse{
    public AllStatisticResponse(int postsCount, int likesCount, int dislikesCount, int viewsCount, long firstPublication) {
        super(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }
}
