package com.ikkiking.base.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;


@Slf4j
public class CloudUtil extends StorageUtil {
    public CloudUtil(String uploadPath,
                     MultipartFile multipartFile) {
        super(uploadPath, multipartFile);
    }

    @Override
    public String uploadImage() throws IOException {
        Cloudinary cloudinary = new Cloudinary(uploadPath);
        File file = Files.createTempFile("temp", multipartFile.getOriginalFilename()).toFile();
        multipartFile.transferTo(file);
        return upload(file, cloudinary);
    }

    @Override
    public String uploadPhoto(int width, int height) throws IOException {
        Cloudinary cloudinary = new Cloudinary(uploadPath);
        cloudinary.url().transformation(new Transformation().width(width).height(height).crop("scale"));
        File file = Files.createTempFile("temp", multipartFile.getOriginalFilename()).toFile();
        multipartFile.transferTo(file);
        return upload(file, cloudinary);
    }

    /**
     * Загружаем изображение в облако, в случае неудачи отдаём относительну ссылку на сервере.
     */
    private String upload(File file,
                          Cloudinary cloudinary) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}
