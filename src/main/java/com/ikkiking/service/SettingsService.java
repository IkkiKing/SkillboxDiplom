package com.ikkiking.service;

import com.ikkiking.api.request.SettingsRequest;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.repository.GlobalSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    /**
     * Глобальные настройки блога.
     */
    public ResponseEntity<SettingsResponse> globalSettings() {

        SettingsResponse settingsResponse = new SettingsResponse();

        List<GlobalSettings> globalSettingsList = globalSettingsRepository.findAll();

        globalSettingsList.forEach(setting -> {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    settingsResponse.setMultiUserMode(setting.getValue().equals("YES"));
                    break;
                case "POST_PREMODERATION":
                    settingsResponse.setPostPremoderation(setting.getValue().equals("YES"));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    settingsResponse.setStatisticsIsPublic(setting.getValue().equals("YES"));
                    break;
                default:
                    log.warn("UNKNOWN SETTING");
            }
        });
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
     */
    @Transactional
    public void settings(@RequestBody SettingsRequest settingsRequest) {
        List<GlobalSettings> globalSettings = globalSettingsRepository.findAll();
        globalSettings.forEach(setting -> {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    setting.setValue(settingsRequest.isMultiUserMode() ? "YES" : "NO");
                    break;
                case "POST_PREMODERATION":
                    setting.setValue(settingsRequest.isPostPreModeration() ? "YES" : "NO");
                    break;
                case "STATISTICS_IS_PUBLIC":
                    setting.setValue(settingsRequest.isStatisticIsPublic() ? "YES" : "NO");
                    break;
                default:
                    log.warn("UNKNOWN SETTING");
            }
        });
        globalSettingsRepository.saveAll(globalSettings);
    }


}
