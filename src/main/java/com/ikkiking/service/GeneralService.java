package com.ikkiking.service;

import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.request.ProfileRequest;
import com.ikkiking.api.response.ModerationResponse;
import com.ikkiking.api.response.ProfileErrorResponse;
import com.ikkiking.api.response.ProfileResponse;
import com.ikkiking.api.response.statistic.StatisticResponse;
import com.ikkiking.base.ContextUser;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GeneralService {

    @Value("${password.min.length}")
    private int passwordMinLength;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final StorageService storageService;

    @Autowired
    public GeneralService(PostRepository postRepository,
                          UserRepository userRepository,
                          GlobalSettingsRepository globalSettingsRepository,
                          StorageService storageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.storageService = storageService;
    }

    /**
     * Возвращает статистику текущего пользователя по блогу.
     */
    public ResponseEntity<StatisticResponse> myStatistic() {
        User user = ContextUser.getUserFromContext(userRepository);
        StatisticCustom statisticCustom = postRepository.findMyStatisticByUserId(user.getId());

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    /**
     * Возвращает всю статистику по блогу.
     */
    public ResponseEntity<StatisticResponse> allStatistic() {
        GlobalSettings globalSettings = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC").orElseThrow(() ->
                new SettingNotFoundException("Check statistic settings. STATISTICS_IS_PUBLIC not found in db"));

        //В случае если публичный показ статистики запрещен и юзер не модератор
        if (globalSettings.getValue().equals("NO")) {
            User user = ContextUser.getUser(userRepository).orElseThrow(() ->
                new StatisticAccessException("Statistic is not public!"));

            if (!user.isModerator()) {
                throw new StatisticAccessException("Statistic is not public!");
            }
        }
        StatisticCustom statisticCustom = postRepository.findAllStatistic();

        return ResponseEntity.ok(getStatisticResponse(statisticCustom));
    }

    /**
     * Вспомогательный метод формирующий ResponseObject для статистики.
     */
    private StatisticResponse getStatisticResponse(StatisticCustom statistic) {
        Long likesCount = statistic.getLikesCount() == null
                ? 0 : statistic.getLikesCount();
        Long dislikesCount = statistic.getDislikesCount() == null
                ? 0 : statistic.getDislikesCount();
        Long viewsCount = statistic.getViewsCount() == null
                ? 0 : statistic.getViewsCount();
        Long firstPublication = statistic.getFirstPublication() == null
                ? null : TimeUnit.MILLISECONDS.toSeconds(
                        statistic.getFirstPublication().getTime());

        return new StatisticResponse(
                statistic.getPostsCount(),
                likesCount,
                dislikesCount,
                viewsCount,
                firstPublication);
    }

    /**
     * Модерация постов.
     */
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
     */
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
     */
    public ResponseEntity<Object> image(MultipartFile multipartFile) {
        return ResponseEntity.ok(storageService.uploadImage(multipartFile));
    }

    /**
     * Редактирование профиля.
     *
     * @param photo       аватар
     * @param name        логин
     * @param email       e-mail
     * @param removePhoto признак необходимости удаления фото
     * @param password    пароль
     */
    @Transactional
    public ResponseEntity<ProfileResponse> profileMulti(MultipartFile photo,
                                                        String name,
                                                        String email,
                                                        String removePhoto,
                                                        String password) {
        String filePath = storageService.uploadPhoto(photo);

        ProfileRequest profileRequest = new ProfileRequest(
                filePath,
                name,
                email,
                password,
                removePhoto.equals("1") ? 1 : 0);

        editProfile(profileRequest);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Редактирование профиля(без изображения).
     */
    @Transactional
    public ResponseEntity<ProfileResponse> profile(ProfileRequest profileRequest) {
        editProfile(profileRequest);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Вспомогательный метод редактирования профиля.
     */
    private void editProfile(ProfileRequest profileRequest) {

        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        String name = profileRequest.getName();
        User user = ContextUser.getUserFromContext(userRepository);

        validateCredentials(user, name, email, password);

        user.setName(name);
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
     * Валидация данных пользователя.
     */
    private void validateCredentials(User user,
                                     String name,
                                     String email,
                                     String password) {

        ProfileErrorResponse profileErrorResponse = new ProfileErrorResponse();
        //Если пароль есть, проверим его длину
        if (name == null || name.isEmpty()) {
            profileErrorResponse.setName("Имя указано неверно");
        }

        if (password != null && password.isEmpty()) {
            if (password.length() < passwordMinLength) {
                profileErrorResponse.setPassword("Пароль короче " + passwordMinLength + " символов");
            }
        }
        //Если текущий email не совпадает с указанным
        if (!user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                profileErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
            }
        }
        if (profileErrorResponse.getName() != null
                || profileErrorResponse.getPassword() != null
                || profileErrorResponse.getEmail() != null) {
            throw new ProfileException(profileErrorResponse);
        }
    }


}
