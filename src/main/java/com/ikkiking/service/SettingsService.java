package com.ikkiking.service;

import com.ikkiking.api.request.SettingsRequest;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    /**
     * Глобальные настройки блога.
     */
    public ResponseEntity<SettingsResponse> globalSettings() {

        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setStatisticsIsPublic(getSettingsValue(globalSettingsRepository, "MULTIUSER_MODE"));
        settingsResponse.setMultiUserMode(getSettingsValue(globalSettingsRepository, "POST_PREMODERATION"));
        settingsResponse.setPostPremoderation(getSettingsValue(globalSettingsRepository, "STATISTICS_IS_PUBLIC"));

        return ResponseEntity.ok(settingsResponse);
    }

    /**
     * Вспомогательный метод получения настройки из БД.
     */
    public static boolean getSettingsValue(GlobalSettingsRepository globalSettingsRepository,
                                           String code) {
        boolean value = false;
        Optional<GlobalSettings> valueDb = globalSettingsRepository.findByCode(code);
        if (valueDb.isPresent()) {
            if (valueDb.get().getValue().equals("YES")) {
                value = true;
            }
        }
        return value;
    }

    /**
     * Управление настройками блога.
     * */
    @Transactional
    public void settings(@RequestBody SettingsRequest settingsRequest) {
        setSetting("MULTIUSER_MODE", settingsRequest.isMultiUserMode() ? "YES" : "NO");
        setSetting("POST_PREMODERATION", settingsRequest.isPostPreModeration() ? "YES" : "NO");
        setSetting("STATISTICS_IS_PUBLIC", settingsRequest.isStatisticIsPublic() ? "YES" : "NO");
    }

    /**
     * Вспомогательный метод управлений настройкой.
     * @param code Код настройки
     * @param value Значение настройки
     * */
    private void setSetting(String code, String value) {
        Optional<GlobalSettings> globalSettingsOptional = globalSettingsRepository.findByCode(code);
        if (globalSettingsOptional.isPresent()) {
            GlobalSettings globalSettings = globalSettingsOptional.get();
            globalSettings.setValue(value);
            globalSettingsRepository.save(globalSettings);
        }
    }
}
