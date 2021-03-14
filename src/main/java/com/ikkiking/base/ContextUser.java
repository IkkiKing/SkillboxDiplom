package com.ikkiking.base;

import com.ikkiking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

@Slf4j
public class ContextUser {

    /**
     * Возвращает email авторизованного пользователя.
     */
    public static String getEmailFromContext() {
        String email = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getPrincipal().equals("anonymousUser")) {
            User user = (User) auth.getPrincipal();
            email = user.getUsername();
        }
        return email;
    }

    /**
     * Возвращает авторизованного пользователя.
     */
    public static com.ikkiking.model.User getUserFromContext(UserRepository userRepository)
            throws UsernameNotFoundException {

        String email = ContextUser.getEmailFromContext();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user " + email + " not found")
        );
    }

    public static Optional<com.ikkiking.model.User> getUser(UserRepository userRepository) {
        String email = ContextUser.getEmailFromContext();
        return userRepository.findByEmail(email);
    }
}
