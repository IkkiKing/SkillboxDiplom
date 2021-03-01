package com.ikkiking.base;

import com.github.cage.Cage;
import com.github.cage.IGenerator;
import com.github.cage.image.Painter;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Base64;
import java.util.Random;

@Data
public class CaptchaUtil {

    private int captchaLength;
    private int captchaWidth;
    private int captchaHeight;

    private String code;
    private String secretCode;
    private String imageString;

    public CaptchaUtil(int captchaLength, int captchaWidth, int captchaHeight) {
        this.captchaLength = captchaLength;
        this.captchaWidth = captchaWidth;
        this.captchaHeight = captchaHeight;
        setupCaptchaUtil();
    }

    /**
     * Возвращает объект для формирования капчи.
     * */
    private void setupCaptchaUtil(){
        IGenerator<String> iGenerator = () ->  RandomStringUtils.random(captchaLength, true, true);
        Painter painter = new Painter(
                captchaWidth,
                captchaHeight,
                null,
                null,
                null,
                new Random(4));
        Cage cage = new Cage(
                painter,
                null,
                null,
                "png",
                0.5f,
                iGenerator,
                null);

        code = cage.getTokenGenerator().next();
        secretCode = RandomStringUtils.random(20, true, true);//TODO: 20 move in config variable
        imageString = "data:image/png;base64, " + Base64.getEncoder().encodeToString(cage.draw(code));
    }
}
