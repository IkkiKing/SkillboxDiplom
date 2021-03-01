package com.ikkiking.service;

import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.request.ProfileRequest;
import com.ikkiking.api.response.*;
import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.ImageUtil;
import com.ikkiking.base.exception.ImageUploadException;
import com.ikkiking.base.exception.ProfileException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@Service
@Slf4j
public class GeneralService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Value("${image.filepath}")
    private final static String imageDir = "src/main/resources/upload";

    @Value("${avatar.filePath}")
    private final static String avatarDir = "src/main/resources/upload/avatar";

    @Value("${avatar.fileSize}")
    private final static long maxFileSize = 5;

    @Autowired
    public GeneralService(PostRepository postRepository,
                          UserRepository userRepository,
                          GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    /**
     * Возвращает статистику текущего пользователя по блогу.
     * */
    public ResponseEntity<StatisticResponse> myStatistic() {

        User user = ContextUser.getUserFromContext(userRepository);
        StatisticCustom statisticCustom = postRepository.findMyStatisticByUserId(user.getId());

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    /**
     * Возвращает всю статистику по блогу.
     * */
    public ResponseEntity<StatisticResponse> allStatistic() {

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

    /**
     * Вспомогательный метод формирующий ResponseObject для статистики.
     * */
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

    /**
     * Модерация постов.
     * */
    @Transactional
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
        }
        return ResponseEntity.ok(moderationResponse);
    }

    /**
     * Вспомогательный метод, определяет статус модерации по переданному решению.
     * */
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

    /**
     * Загрузка изображения.
     * */
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
            throw new ImageUploadException(imageResponse);
        }

        try {
            imageUtil.uploadImage();
            imageResponse.setResult(true);
        } catch (IOException ex) {
            log.error("Error file uploading");
            imageResponse.setErrors(new ImageErrorResponse("Ошибка загрузки файла на сервер"));
            throw new ImageUploadException(imageResponse);
        }

        return ResponseEntity.ok(imageUtil.getImagePath());
    }

    /**
     * Редактирование профиля.
     * @param photo аватар
     * @param name логин
     * @param email e-mail
     * @param removePhoto признак необходимости удаления фото
     * @param password пароль
     * */
    @Transactional
    public ResponseEntity<ProfileResponse> profileMulti(MultipartFile photo,
                                                        String name,
                                                        String email,
                                                        String removePhoto,
                                                        String password) {
        ImageUtil imageUtil = new ImageUtil(
                photo,
                20,
                true,
                true,
                avatarDir
        );

        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();

        if (photo.getSize() / 1_000_000 > maxFileSize) {
            log.warn("photo size is over limit");
            profileErrorResponse.setPhoto("Файл превышает допустимый размер " + maxFileSize + " Мб");
            throw new ProfileException(profileErrorResponse);
        }
        if (imageUtil.getFormatName().equals("unknown")) {
            log.warn("photo format is unknown");
            profileErrorResponse.setPhoto("Выбран не поддерживаемый тип файла");
            throw new ProfileException(profileErrorResponse);
        }

        try {
            imageUtil.uploadAvatar();
        } catch (IOException ex) {
            log.error("Error photo uploading");
            profileErrorResponse.setPhoto("Ошибка загрузки файла на сервер");
            throw new ProfileException(profileErrorResponse);
        }

        ProfileRequest profileRequest = new ProfileRequest(
                imageUtil.getImagePath(),
                name,
                email,
                password,
                removePhoto.equals("1") ? 1 : 0
        );

        editProfile(profileRequest);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Редактирование профиля(без изображения).
     * */
    @Transactional
    public ResponseEntity<ProfileResponse> profile(ProfileRequest profileRequest) {
        editProfile(profileRequest);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Вспомогательный метод редактирования профиля.
     * */
    private void editProfile(ProfileRequest profileRequest) {

        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        User user = ContextUser.getUserFromContext(userRepository);

        validateCredentials(user, email, password);

        user.setName(profileRequest.getName());
        user.setEmail(email);
        if (profileRequest.getRemovePhoto() != null) {
            if (profileRequest.getRemovePhoto() == 1) {
                user.setPhoto(null);
            }
        }
        if (password != null) {
            user.setPassword(SecurityConfig.passwordEncoder()
                    .encode(password));
        }
        if (profileRequest.getPhoto() != null) {
            user.setPhoto(profileRequest.getPhoto());
        }
        userRepository.save(user);
    }

    /**
     * Валидация данных пользователя
     * */
    private void validateCredentials(User user,
                                     String email,
                                     String password) {

        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();
        //Если пароль есть, проверим его длину
        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                profileErrorResponse.setPassword("Пароль короче 6 символов");
                throw new ProfileException(profileErrorResponse);
            }
        }
        //Если текущий email не совпадает с указанным
        if (!user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                profileErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
                throw new ProfileException(profileErrorResponse);
            }
        }
    }
}
