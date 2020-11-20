package com.ikkiking.service;

import com.ikkiking.api.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public SettingsResponse getGlobalSettings(){
      SettingsResponse settingsResponse = new SettingsResponse();
      settingsResponse.setMultiUserMode(true);
      settingsResponse.setStatisticIsPublic(true);
      return settingsResponse;
    }
}
