package com.ikkiking.repository;


import com.ikkiking.model.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCodes, Long> {
    int countByCodeAndSecretCode(String code, String secretCode);

    @Modifying
    @Query(value = "delete  from captcha_codes cc " +
            "where cc.time < DATE_SUB(sysdate(),INTERVAL 1 HOUR)",
    nativeQuery = true)
    void deleteOldCaptcha(int hours);
}
