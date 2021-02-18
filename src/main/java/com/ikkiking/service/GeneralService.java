package com.ikkiking.service;

import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.request.ProfileRequest;
import com.ikkiking.api.response.*;
import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.ImageUtil;
import com.ikkiking.base.exception.SettingNotFoundException;
import com.ikkiking.base.exception.StatisticAccessException;
import com.ikkiking.config.SecurityConfig;
import com.ikkiking.model.GlobalSettings;
import com.ikkiking.model.ModerationStatus;
import com.ikkiking.model.Post;
import com.ikkiking.model.User;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.StatisticCustom;
import com.ikkiking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@Service
@Slf4j
public class GeneralService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;


    /*TODO не подтягивается конфигурация разобраться с этим
    *
    * */
    //@Value("${image.filepath}")
    private final static String imageDir = "src/main/resources/upload";

    //@Value("${avatar.filePath}")
    private final static String avatarDir = "src/main/resources/upload/avatar";

    //@Value("${avatar.fileSize}")
    private final static long maxFileSize = 5;

    @Autowired
    public GeneralService(PostRepository postRepository, UserRepository userRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    private StatisticResponse getStatisticResponse(StatisticCustom statistic) {
        if (statistic.getPostsCount() == 0) {
            log.info("Posts count equal zero");
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

        GlobalSettings globalSettings = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC").orElseThrow(
                () -> new SettingNotFoundException("Check statistic settings")
        );


        //В случае если публичный показ статистики запрещен и юзер не модератор
        if (globalSettings.getValue().equals("NO") && !user.isModerator()) {
            throw new StatisticAccessException("Statistic is not public!");
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
            post.setModerator(user);
            post.setModerationStatus(moderationStatus);
            postRepository.save(post);

        } else {
            log.warn("post from moderation request wasnt found or already moderated");
            moderationResponse.setResult(false);
        }
        return ResponseEntity.ok(moderationResponse);
    }


    public ResponseEntity<Object> image(MultipartFile multipartFile) {

        ImageResponse imageResponse = new ImageResponse();

        ImageUtil imageUtil = new ImageUtil(
                multipartFile,
                20,
                true,
                false,
                imageDir
        );

        if (imageUtil.getFormatName().equals("unknown")) {
            log.error("Unknown image format file");
            imageResponse.setErrors(new ImageErrorResponse("Выбран не поддерживаемый тип файла"));
            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            imageUtil.uploadImage();
            imageResponse.setResult(true);
        } catch (IOException ex) {
            log.error("Error file uploading");
            imageResponse.setResult(false);
            imageResponse.setErrors(new ImageErrorResponse("Ошибка загрузки файла на сервер"));
            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(imageUtil.getImagePath());
    }


    public ResponseEntity<ProfileResponse> profileMulti(MultipartFile photo,
                                                        String name,
                                                        String email,
                                                        String removePhoto,
                                                        String password) {

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();


        ImageUtil imageUtil = new ImageUtil(
                photo,
                20,
                true,
                true,
                avatarDir
        );

        if (photo.getSize() / 1_000_000 > maxFileSize) {
            log.warn("photo size is over limit");

            profileErrorResponse.setPhoto("Файл превышает допустимый размер " + maxFileSize + " Мб");
            profileResponse.setResult(false);

        } else {

            if (imageUtil.getFormatName().equals("unknown")) {
                log.warn("photo format is unknown");
                profileErrorResponse.setPhoto("Выбран не поддерживаемый тип файла");
                profileResponse.setResult(false);
            } else {

                try {
                    imageUtil.uploadAvatar();
                } catch (IOException ex) {
                    log.error("Error photo uploading");
                    profileErrorResponse.setPhoto("Ошибка загрузки файла на сервер");
                    profileResponse.setResult(false);
                    ex.printStackTrace();
                }
            }
        }

        ProfileRequest profileRequest = new ProfileRequest(
                imageUtil.getImagePath(),
                name,
                email,
                password,
                removePhoto.equals("1") ? 1 : 0
        );

        editProfile(profileResponse, profileErrorResponse, profileRequest);

        return ResponseEntity.ok(profileResponse);
    }


    public ResponseEntity<ProfileResponse> profile(ProfileRequest profileRequest) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();

        editProfile(profileResponse, profileErrorResponse, profileRequest);

        return ResponseEntity.ok(profileResponse);
    }

    private void editProfile(ProfileResponse profileResponse,
                             ProfileErrorResponse profileErrorResponse,
                             ProfileRequest profileRequest) {

        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        Integer removePhoto = profileRequest.getRemovePhoto();
        String photo = profileRequest.getPhoto();

        User user = ContextUser.getUserFromContext(userRepository);

        //Если пароль есть, проверим его длину
        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                profileErrorResponse.setPassword("Пароль короче 6 символов");
                profileResponse.setResult(false);
            }
        }

        //Если текущий email не совпадает с указанным
        if (!user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                profileErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
                profileResponse.setResult(false);
            }
        }

        if (profileResponse.isResult()) {

            user.setName(name);
            user.setEmail(email);

            if (removePhoto != null) {
                if (removePhoto == 1) {
                    user.setPhoto(null);
                }
            }

            if (password != null) {
                user.setPassword(SecurityConfig.passwordEncoder()
                        .encode(password));
            }

            if (photo != null) {
                user.setPhoto(photo);
            }

            userRepository.save(user);
        } else {
            profileResponse.setErrors(profileErrorResponse);
        }
    }


}
