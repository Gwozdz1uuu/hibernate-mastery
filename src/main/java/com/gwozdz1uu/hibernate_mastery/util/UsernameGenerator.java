package com.gwozdz1uu.hibernate_mastery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class UsernameGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    public String generateUniqueUsername(
            String firstName,
            String lastName,
            Predicate<String> existsCheck
    ) {
        String base = firstName + "." + lastName;
        if (!existsCheck.test(base)) {
            logger.debug("Generated username: {}", base);
            return base;
        }

        int serial = 1;
        while (existsCheck.test(base + serial)) {
            serial++;
        }
        String generatedUsername = base + serial;
        logger.debug("Username collision detected for '{}', generated: {}", base, generatedUsername);
        return generatedUsername;
    }
}
