package com.ikkiking.service;

import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {

        SettingsResponse settingsResponse = new SettingsResponse();

        settingsResponse.setStatisticIsPublic(getSettingsValue(globalSettingsRepository, "MULTIUSER_MODE"));
        settingsResponse.setMultiUserMode(getSettingsValue(globalSettingsRepository, "POST_PREMODERATION"));
        settingsResponse.setPostPremoderation(getSettingsValue(globalSettingsRepository, "STATISTICS_IS_PUBLIC"));

        return settingsResponse;
    }

    private static boolean getSettingsValue(GlobalSettingsRepository globalSettingsRepository,
                                            String code){

        Optional<GlobalSettings> valueDb = globalSettingsRepository.findByCode(code);

        boolean value = true;
        if (valueDb.isPresent()){
            if (valueDb.get().getValue().equals("NO")){
                value = false;
            }
        }
        return value;
    }
}
