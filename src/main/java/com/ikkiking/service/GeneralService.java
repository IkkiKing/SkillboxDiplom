package com.ikkiking.service;

import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.DateHelper;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.model.User;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.StatisticCustom;
import com.ikkiking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class GeneralService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public GeneralService(PostRepository postRepository, UserRepository userRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }


    private StatisticResponse getStatisticResponse(StatisticCustom statistic){
        if (statistic.getPostsCount() == 0){
            return new StatisticResponse(
                    0l,
                    0l,
                    0l,
                    0l,
                    null);
        }else{
            return new StatisticResponse(
                    statistic.getPostsCount(),
                    statistic.getLikesCount(),
                    statistic.getDislikesCount(),
                    statistic.getViewsCount(),
                    statistic.getFirstPublication().getTime() / 1000l);
        }
    }

    public ResponseEntity<StatisticResponse> getMyStatistic(){

        User user = ContextUser.getUserFromContext(userRepository);

        StatisticCustom statisticCustom = postRepository.findByUserId(user.getId());

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    public ResponseEntity<StatisticResponse> getAllStatistic(){

        User user = ContextUser.getUserFromContext(userRepository);

        Optional<GlobalSettings> globalSettings = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");

        if(globalSettings.isPresent()){
            //В случае если публичный показ статистики запрещен и юзер не модератор
            if (globalSettings.get().getValue().equals("NO") && !user.isModerator()){
                return new ResponseEntity<>(new StatisticResponse(), HttpStatus.UNAUTHORIZED);
            }

        }else{
            /*TODO: Exception?*/
        }

        StatisticCustom statisticCustom = postRepository.findAllStatistic();

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }


}
