package com.ikkiking.repository;

import java.util.Date;

public interface StatisticCustom {
    Long getPostsCount();

    Long getLikesCount();

    Long getDislikesCount();

    Long getViewsCount();

    Date getFirstPublication();
}
