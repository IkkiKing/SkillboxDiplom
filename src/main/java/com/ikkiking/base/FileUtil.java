package com.ikkiking.base;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Data
public class FileUtil {

    private final MultipartFile multipartFile;
    private final String fileDir;
    private final int nameLength;

    private String formatName;
    private String filePath;

    public FileUtil(MultipartFile multipartFile,
                    String fileDir,
                    int nameLength) {
        this.multipartFile = multipartFile;
        this.fileDir = fileDir;
        this.nameLength = nameLength;
        getImageFileExtension();
    }

    /**
     * Определяет тип изображения MultiPartFile
     * */
    private String getImageFileExtension() {
        formatName = "unknown";
        if (multipartFile.getContentType().equals("image/jpeg")) {
            formatName = "jpg";
        }
        if (multipartFile.getContentType().equals("image/png")) {
            formatName = "png";
        }
        return formatName;
    }
    /**
     * Метод формирования директорий для хранения изображений.
     *
     * @param imageFilePath Путь для сохранения изображения
     * @param randomString Строка из которой формируются названия директорий
     * */
    private String createRandomDirs(String imageFilePath, String randomString) {
        int i = 0;

        StringBuilder builder = new StringBuilder();
        builder.append(imageFilePath);

        while (i < 6) {

            builder.append("/" + randomString.substring(i, i + 2));
            File theDir = new File(builder.toString());
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            i += 2;
        }
        return builder.toString();
    }

    /**
     * Сохранение изображения на сервер.
     *
     * @param uploadDir директория для сохранения
     * @param fileName имя файла
     * */
    private void saveFile(String uploadDir,
                          String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * Загрузка изображения на сервер.
     * */
    public void uploadImage() throws IOException {

        String randomString = RandomStringUtils.random(nameLength, true, false);

        String filePath = createRandomDirs(fileDir, randomString);

        String fileName = randomString.substring(8) + "." + formatName;

        filePath = new File(filePath + "/" + fileName).getAbsolutePath();

        saveFile(filePath, fileName);
    }

    /**
     * Загрузка фото на сервер в сжатом объеме.
     *
     * @param width ширина для сжатия
     * @param height высота для сжатия
     * */
    public void uploadPhoto(final int width,
                            final int height) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        bufferedImage = Scalr.resize(bufferedImage, width, height);

        String randomString = RandomStringUtils.random(nameLength, true, false);

        File file = new File(fileDir + "/" + randomString + "." + formatName);

        if (!file.exists()) {
            file.mkdirs();
        }

        ImageIO.write(bufferedImage, formatName, file);
        filePath = file.getAbsolutePath();
    }
}
