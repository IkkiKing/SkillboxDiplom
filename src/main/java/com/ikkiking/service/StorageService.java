package com.ikkiking.service;

import com.ikkiking.api.response.ImageErrorResponse;
import com.ikkiking.api.response.ImageResponse;
import com.ikkiking.api.response.ProfileErrorResponse;
import com.ikkiking.base.exception.ImageUploadException;
import com.ikkiking.base.exception.ProfileException;
import com.ikkiking.base.storage.CloudUtil;
import com.ikkiking.base.storage.FileUtil;
import com.ikkiking.base.storage.StorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@Slf4j
public class StorageService {

    @Value("${file.path}")
    private String filePath;

    @Value("${storage.mode}")
    private String mode;

    @Value("${file.name.length}")
    private int fileNameLength;

    @Value("${file.photo.size}")
    private long maxPhotoSize;

    @Value("${file.photo.width}")
    private int photoWidth;

    @Value("${file.photo.height}")
    private int photoHeight;

    /**
     * Метод загрузки изображения в хранилище.
     *
     * @param image изображение полученное с фронта
     * @return путь к загруженному изображению
     *
     * */
    public String uploadImage(MultipartFile image) {
        ImageResponse imageResponse = new ImageResponse();

        StorageUtil storageUtil = getStorageUtil(image);

        if (storageUtil.getFormatName().equals("unknown")) {
            log.error("Unknown image format file");
            imageResponse.setErrors(new ImageErrorResponse("Выбран не поддерживаемый тип файла"));
            throw new ImageUploadException(imageResponse);
        }
        String filePath;
        try {
            filePath = storageUtil.uploadImage();
        } catch (IOException ex) {
            log.error("Error file uploading");
            imageResponse.setErrors(new ImageErrorResponse("Ошибка загрузки файла на сервер"));
            throw new ImageUploadException(imageResponse);
        }
        log.info("File upload success: " + filePath);
        return filePath;
    }

    /**
     * Метод загрузки фото в хранилище.
     *
     * @param photo изображение полученное с фронта
     * @return путь к загруженному изображению
     *
     * */
    public String uploadPhoto(MultipartFile photo) {
        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();

        if (photo.getSize() > maxPhotoSize) {
            log.warn("photo size is over limit");
            profileErrorResponse.setPhoto("Файл превышает допустимый размер " + maxPhotoSize + " Мб");
            throw new ProfileException(profileErrorResponse);
        }

        StorageUtil storageUtil = getStorageUtil(photo);

        if (storageUtil.getFormatName().equals("unknown")) {
            log.warn("photo format is unknown");
            profileErrorResponse.setPhoto("Выбран не поддерживаемый тип файла");
            throw new ProfileException(profileErrorResponse);
        }

        String filePath;
        try {
            filePath = storageUtil.uploadPhoto(photoWidth, photoHeight);
        } catch (IOException ex) {
            log.error("Error photo uploading");
            profileErrorResponse.setPhoto("Ошибка загрузки файла на сервер");
            throw new ProfileException(profileErrorResponse);
        }
        log.info("File upload success: " + filePath);
        return filePath;
    }

    /**
     * Возвращает экземпляр класса определяющий способ загрузки: локальное хранилище/облако.
     *
     * @param multipartFile изображение
     * @return возвращает тип хранилища для сохранения файла
     * */
    private StorageUtil getStorageUtil(MultipartFile multipartFile) {
        StorageUtil storageUtil;

        switch (mode) {
            case "cloud":
                storageUtil = new CloudUtil(System.getenv("CLOUDINARY_URL"),
                        multipartFile);
                break;
            default:
                storageUtil = new FileUtil(System.getProperty("user.dir") + filePath,
                        multipartFile,
                        fileNameLength);
        }
        return storageUtil;
    }

}
