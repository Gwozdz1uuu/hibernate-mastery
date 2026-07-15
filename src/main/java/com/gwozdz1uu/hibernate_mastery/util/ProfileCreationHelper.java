package com.gwozdz1uu.hibernate_mastery.util;

import com.gwozdz1uu.hibernate_mastery.entity.User;
import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class ProfileCreationHelper {

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public String setupNewProfile(User user, String firstName, String lastName, Predicate<String> usernameExists) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        user.setUsername(usernameGenerator.generateUniqueUsername(firstName, lastName, usernameExists));
        String rawPassword = passwordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        return rawPassword;
    }
}
