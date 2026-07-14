package com.gwozdz1uu.hibernate_mastery.config;

import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import com.gwozdz1uu.hibernate_mastery.security.PlainTextPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder(); // swap to BCryptPasswordEncoder when ready
    }
}
