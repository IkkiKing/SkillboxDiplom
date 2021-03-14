package com.ikkiking.base.storage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Data
public abstract class StorageUtil {

    protected final MultipartFile multipartFile;
    protected final String uploadPath;

    protected String formatName;

    public StorageUtil(String uploadPath, MultipartFile multipartFile) {
        this.uploadPath = uploadPath;
        this.multipartFile = multipartFile;
        calculateFormatName();
    }

    /**
     * Определяет тип изображения MultiPartFile.
     */
    private void calculateFormatName() {
        formatName = "unknown";
        if (multipartFile.getContentType().equals("image/jpeg")) {
            formatName = "jpg";
        }
        if (multipartFile.getContentType().equals("image/png")) {
            formatName = "png";
        }
    }

    /**
     * Загрузка изображения в файловое хранилище.
     * @return путь к файлу
     * */
    public abstract String uploadImage() throws IOException;

    /**
     * Загрузка фото на сервер в сжатом объеме.
     *
     * @param width  ширина для сжатия
     * @param height высота для сжатия
     * @return путь к файлу
     */
    public abstract String uploadPhoto(final int width,
                                       final int height) throws IOException;
}
