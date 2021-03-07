package com.ikkiking.base;


import com.ikkiking.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ContextUser {

    /**
     * Возвращает email авторизованного пользователя.
     * */
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
     * */
    public static com.ikkiking.model.User getUserFromContext(UserRepository userRepository)
            throws UsernameNotFoundException {

        String email = ContextUser.getEmailFromContext();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user " + email + " not found")
        );
    }
}
