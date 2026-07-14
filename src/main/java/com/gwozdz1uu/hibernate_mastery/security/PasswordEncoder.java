package com.gwozdz1uu.hibernate_mastery.security;

public interface PasswordEncoder {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
