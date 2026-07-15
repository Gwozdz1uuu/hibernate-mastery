package com.gwozdz1uu.hibernate_mastery.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void encodeAndMatch_shouldWork() {
        String encoded = encoder.encode("secret123");
        assertNotEquals("secret123", encoded);
        assertTrue(encoder.matches("secret123", encoded));
        assertFalse(encoder.matches("wrong", encoded));
    }
}
