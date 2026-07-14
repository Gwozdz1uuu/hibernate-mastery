package com.gwozdz1uu.hibernate_mastery.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
