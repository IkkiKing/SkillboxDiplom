package com.ikkiking.service;

import com.ikkiking.api.response.StatisticResponse.AllStatisticResponse;
import com.ikkiking.api.response.StatisticResponse.MyStatisticResponse;

import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    public MyStatisticResponse getMyStatistic(){
        return new MyStatisticResponse(10, 5, 2, 11, 1590217200);
    }

    public AllStatisticResponse getAllStatistic(){
        return new AllStatisticResponse(10, 5, 2, 11, 1590217200);
    }

}
