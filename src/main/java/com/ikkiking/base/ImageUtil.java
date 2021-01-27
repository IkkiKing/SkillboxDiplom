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
public class ImageUtil {

    private MultipartFile multipartFile;
    private int fileNameLength;
    private boolean withLetters;
    private boolean withNumbers;

    private String imageDir;
    private String formatName;
    private String imagePath;


    public ImageUtil(MultipartFile multipartFile,
                     int fileNameLength,
                     boolean withLetters,
                     boolean withNumbers,
                     String imageDir) {

        this.multipartFile = multipartFile;
        this.fileNameLength = fileNameLength;
        this.withLetters = withLetters;
        this.withNumbers = withNumbers;
        this.imageDir = imageDir;
        getImageFileExtension();
    }

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

    public void uploadAvatar() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        bufferedImage = Scalr.resize(bufferedImage, 36, 36);

        String randomString = RandomStringUtils.random(20, true, false);

        File file = new File(imageDir + "/" + randomString + "." + formatName);

        if (!file.exists()) {
            file.mkdirs();
        }

        ImageIO.write(bufferedImage, formatName, file);

        imagePath = file.getAbsolutePath();
    }


    public void uploadImage() throws IOException {

        String randomString = RandomStringUtils.random(20, true, false);

        String filePath = createRandomDirs(imageDir, randomString);

        String fileName = randomString.substring(8) + "." + formatName;

        imagePath = new File(filePath + "/" + fileName).getAbsolutePath();

        saveFile(filePath, fileName, multipartFile);
    }


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

    private void saveFile(String uploadDir,
                                String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    }

}
