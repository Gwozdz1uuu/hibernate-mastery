package com.gwozdz1uu.hibernate_mastery.dto;

import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTrainerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull TrainingType specialization
) {}
