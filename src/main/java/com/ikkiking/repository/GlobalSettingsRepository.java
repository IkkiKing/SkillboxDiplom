package com.ikkiking.repository;

import com.ikkiking.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
    Optional<GlobalSettings> findByCode(String code);
}
