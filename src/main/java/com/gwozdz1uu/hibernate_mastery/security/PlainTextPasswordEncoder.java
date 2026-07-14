package com.gwozdz1uu.hibernate_mastery.security;

import java.util.Objects;

public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return password;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return Objects.equals(rawPassword, encodedPassword);
    }
}
