package com.ikkiking.base.storage;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class FileUtil extends StorageUtil {

    private final int nameLength;

    public FileUtil(String uploadPath,
                    MultipartFile multipartFile,
                    int nameLength) {
        super(uploadPath, multipartFile);
        this.nameLength = nameLength;
    }


    @Override
    public String uploadImage() throws IOException {
        String randomString = getRandomName();
        String folderName = createRandomDirs(uploadPath, randomString);
        String fileName = getRandomName() + "." + formatName;
        return saveFile(folderName, fileName);
    }


    @Override
    public String uploadPhoto(final int width,
                              final int height) throws IOException {

        File file = new File(uploadPath + "/" + getRandomName() + "." + formatName);

        if (!file.exists()) {
            file.mkdirs();
        }
        ImageIO.write(resizeImage(width, height),
                formatName,
                file);
        return toLinkPath(file.getPath());
    }

    /**
     * Метод сжатия изображения.
     *
     * @param width желаемая ширина
     * @param height желаемая высота
     *
     * @return сжатое изображение
     * */
    private BufferedImage resizeImage(final int width,
                                      final int height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        return Scalr.resize(bufferedImage, width, height);
    }

    /**
     * Метод формирования директорий для хранения изображений.
     *
     * @param imageFilePath Путь для сохранения изображения
     * @param randomString  Строка из которой формируются названия директорий
     */
    private String createRandomDirs(String imageFilePath, String randomString) {
        final int folders = 3;
        final int folderName = 2;
        int i = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(imageFilePath);

        while (i < folders * folderName) {

            builder.append("/" + randomString.substring(i, i + folderName));
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
     * @param folderName директория для сохранения
     * @param fileName   имя файла
     * @return относительное имя файла
     */
    private String saveFile(String folderName,
                            String fileName) throws IOException {
        Path folderPath = Paths.get(folderName);

        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }
        Path imagePath = folderPath.resolve(fileName);
        Files.copy(multipartFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return toLinkPath(imagePath.toString());
    }

    /**
     * Метод преобразует текущий путь файла в линк для фронта.
     */
    private String toLinkPath(String path) {
        String linkPath = path;
        if (path.contains("/uploads")) {
            linkPath = path.substring(path.indexOf("/uploads"));
        } else if (path.contains("\\uploads")) {
            linkPath = path.substring(path.indexOf("\\uploads")).replace("\\", "/");
        }
        return linkPath;
    }

    /**
     * Метод генерирует рандомное имя файла из заданного кол-ва символов.
     */
    private String getRandomName() {
        return RandomStringUtils.random(nameLength, true, false);
    }


}
