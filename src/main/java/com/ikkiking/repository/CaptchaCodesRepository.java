package com.ikkiking.repository;


import com.ikkiking.model.CaptchaCodes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {
}
