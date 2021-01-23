package com.ikkiking.service;

import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.response.ImageErrorResponse;
import com.ikkiking.api.response.ImageResponse;
import com.ikkiking.api.response.ModerationResponse;
import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.FileUploadUtil;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.model.ModerationStatus;
import com.ikkiking.model.Post;
import com.ikkiking.model.User;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.StatisticCustom;
import com.ikkiking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


@Service
public class GeneralService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final static String IMAGE_DIR = "src/main/resources/upload";

    @Autowired
    public GeneralService(PostRepository postRepository, UserRepository userRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }


    private StatisticResponse getStatisticResponse(StatisticCustom statistic) {
        if (statistic.getPostsCount() == 0) {
            return new StatisticResponse(
                    0l,
                    0l,
                    0l,
                    0l,
                    null);
        } else {
            return new StatisticResponse(
                    statistic.getPostsCount(),
                    statistic.getLikesCount(),
                    statistic.getDislikesCount(),
                    statistic.getViewsCount(),
                    statistic.getFirstPublication().getTime() / 1000l);
        }
    }

    public ResponseEntity<StatisticResponse> getMyStatistic() {

        User user = ContextUser.getUserFromContext(userRepository);

        StatisticCustom statisticCustom = postRepository.findByUserId(user.getId());

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    public ResponseEntity<StatisticResponse> getAllStatistic() {

        User user = ContextUser.getUserFromContext(userRepository);

        Optional<GlobalSettings> globalSettings = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");

        if (globalSettings.isPresent()) {
            //В случае если публичный показ статистики запрещен и юзер не модератор
            if (globalSettings.get().getValue().equals("NO") && !user.isModerator()) {
                return new ResponseEntity<>(new StatisticResponse(), HttpStatus.UNAUTHORIZED);
            }

        } else {
            /*TODO: Exception?*/
        }

        StatisticCustom statisticCustom = postRepository.findAllStatistic();

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    private ModerationStatus getModerationStatusByDecision(ModerationRequest moderationRequest) {

        ModerationStatus moderationStatus = null;
        if (moderationRequest.getDecision().equals("accept")) {
            moderationStatus = ModerationStatus.ACCEPTED;
        }
        if (moderationRequest.getDecision().equals("decline")) {
            moderationStatus = ModerationStatus.DECLINED;
        }
        return moderationStatus;
    }

    public ResponseEntity<ModerationResponse> moderate(ModerationRequest moderationRequest) {
        ModerationResponse moderationResponse = new ModerationResponse();

        User user = ContextUser.getUserFromContext(userRepository);
        Optional<Post> postOptional = postRepository.findById(moderationRequest.getPostId());

        ModerationStatus moderationStatus = getModerationStatusByDecision(moderationRequest);

        if (postOptional.isPresent() && moderationStatus != null) {

            moderationResponse.setResult(true);

            Post post = postOptional.get();
            post.setUser(user);
            post.setModerationStatus(moderationStatus);
            postRepository.save(post);

        } else {
            moderationResponse.setResult(false);
            /*TODO: Exception?*/
        }
        return ResponseEntity.ok(moderationResponse);
    }

    public ResponseEntity<Object> image(MultipartFile multipartFile) {

        ImageResponse imageResponse = new ImageResponse();

        String fileExtension = null;
        if (multipartFile.getContentType().equals("image/jpeg")) {
            fileExtension = ".jpg";
        } else if (multipartFile.getContentType().equals("image/png")) {
            fileExtension = ".png";
        } else {
            imageResponse.setErrors(new ImageErrorResponse("Выбран не поддерживаемый тип файла"));
            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }


        String randomString = FileUploadUtil.getRandomString();

        String filePath = FileUploadUtil.createRandomDirs(IMAGE_DIR, randomString);

        String fileName = randomString.substring(8) + fileExtension;

        String imagePath = new File(filePath + "/" + fileName).getAbsolutePath();


        try {
            FileUploadUtil.saveFile(filePath,
                    fileName,
                    multipartFile);
        } catch (IOException ex) {

            System.out.println("Error of upload image " + imagePath);
            ex.printStackTrace();

            imageResponse.setResult(false);
            imageResponse.setErrors(new ImageErrorResponse("Ошибка загрузки файла на сервер"));

            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(imagePath);
    }


}
