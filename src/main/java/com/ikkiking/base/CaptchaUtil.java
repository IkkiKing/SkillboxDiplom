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

    private final int captchaLength;
    private final int captchaWidth;
    private final int captchaHeight;
    private final int secretCodeLength;

    private String code;
    private String secretCode;
    private String imageString;

    public CaptchaUtil(int captchaLength,
                       int captchaWidth,
                       int captchaHeight,
                       int secretCodeLength) {
        this.captchaLength = captchaLength;
        this.captchaWidth = captchaWidth;
        this.captchaHeight = captchaHeight;
        this.secretCodeLength = secretCodeLength;
        setupCaptchaUtil();
    }

    /**
     * Возвращает объект для формирования капчи.
     * */
    private void setupCaptchaUtil() {
        IGenerator<String> generator = () -> RandomStringUtils.random(captchaLength, true, true);
        Painter painter = new Painter(
                captchaWidth,
                captchaHeight,
                null,
                null,
                null,
                null);
        Cage cage = new Cage(
                painter,
                null,
                null,
                "png",
                0.5F,
                generator,
                null);

        code = cage.getTokenGenerator().next();
        secretCode = RandomStringUtils.random(secretCodeLength, true, true);
        imageString = "data:image/png;base64, " + Base64.getEncoder().encodeToString(cage.draw(code));
    }
}
