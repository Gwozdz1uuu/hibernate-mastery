package com.epam.gym_crm_system.util;

import com.epam.gym_crm_system.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsernameGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    public String generateUsername(String firstName, String lastName, List<? extends User> existingUsers) {
        String baseUsername = firstName + "." + lastName;
        Set<String> existingUsernames = existingUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        if (!existingUsernames.contains(baseUsername)) {
            logger.debug("Generated username: {}", baseUsername);
            return baseUsername;
        }

        int serial = 1;
        while (existingUsernames.contains(baseUsername + serial)) {
            serial++;
        }
        String generatedUsername = baseUsername + serial;
        logger.debug("Username collision detected for '{}', generated: {}", baseUsername, generatedUsername);
        return generatedUsername;
    }
}
