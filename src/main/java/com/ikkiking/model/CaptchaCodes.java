package com.ikkiking.model;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "captcha_codes")
public class CaptchaCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "captcha_codes.time may not be null")
    private Date time;

    @NotNull(message = "captcha_codes.name may not be null")
    private String code;

    @NotNull(message = "captcha_codes.name may not be null")
    @Column(name = "secret_code")
    private String secretCode;

}
