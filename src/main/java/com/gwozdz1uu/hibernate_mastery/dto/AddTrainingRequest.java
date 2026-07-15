package com.gwozdz1uu.hibernate_mastery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AddTrainingRequest(
        @NotBlank String traineeUsername,
        @NotBlank String traineePassword,
        @NotBlank String trainerUsername,
        @NotBlank String trainingName,
        @NotNull Long trainingTypeId,
        @NotNull LocalDate trainingDate,
        @Positive int durationMinutes
) {}
