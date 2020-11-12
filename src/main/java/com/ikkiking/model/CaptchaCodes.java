package com.ikkiking.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity(name = "captcha_codes")
public class CaptchaCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "captcha_codes.time may not be null")
    private Date time;

    @NotNull(message = "captcha_codes.name may not be null")
    private String code;

    @NotNull(message = "captcha_codes.name may not be null")
    @Column(name = "secret_code")
    private String secretCode;

}
