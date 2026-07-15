package com.gwozdz1uu.hibernate_mastery.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateTraineeRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate dateOfBirth,
        String address,
        boolean active
) {}
