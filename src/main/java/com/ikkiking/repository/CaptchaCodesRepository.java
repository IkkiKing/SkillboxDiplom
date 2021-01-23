package com.ikkiking.repository;


import com.ikkiking.model.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCodes, Long> {
    @Query(value = "select * from captcha_codes cc " +
            "where cc.code = :code and cc.secret_code = :secretCode",
            nativeQuery = true)
    int countByCodeAndSecretCode(String code, String secretCode);
}
