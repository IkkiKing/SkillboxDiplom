package com.ikkiking.repository;

import com.ikkiking.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Long> {

    @Query(value = "select * from global_settings gs where gs.code = :code limit 1",
    nativeQuery = true)
    Optional<GlobalSettings> findByCode(String code);
}
