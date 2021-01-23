package com.ikkiking.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikkiking.api.request.SettingsRequest;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
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

    public static boolean getSettingsValue(GlobalSettingsRepository globalSettingsRepository,
                                            String code) {

        Optional<GlobalSettings> valueDb = globalSettingsRepository.findByCode(code);

        boolean value = true;
        if (valueDb.isPresent()) {
            if (valueDb.get().getValue().equals("NO")) {
                value = false;
            }
        }
        return value;
    }

    public void setSettings(@RequestBody SettingsRequest settingsRequest) {
        setSetting("MULTIUSER_MODE", settingsRequest.isMultiUserMode() ? "YES" : "NO");
        setSetting("POST_PREMODERATION", settingsRequest.isPostPreModeration() ? "YES" : "NO");
        setSetting("STATISTICS_IS_PUBLIC", settingsRequest.isStatisticIsPublic() ? "YES" : "NO");
    }

    private void setSetting(String code, String value) {
        Optional<GlobalSettings> globalSettingsOptional = globalSettingsRepository.findByCode(code);
        if (globalSettingsOptional.isPresent()) {
            GlobalSettings globalSettings = globalSettingsOptional.get();
            globalSettings.setValue(value);
            globalSettingsRepository.save(globalSettings);
        }
    }
}
