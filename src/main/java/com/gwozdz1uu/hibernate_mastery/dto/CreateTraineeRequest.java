package com.gwozdz1uu.hibernate_mastery.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateTraineeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate dateOfBirth,
        String address
) {}
