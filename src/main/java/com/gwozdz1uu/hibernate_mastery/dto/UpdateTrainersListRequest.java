package com.gwozdz1uu.hibernate_mastery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateTrainersListRequest(
        @NotBlank String traineeUsername,
        @NotBlank String traineePassword,
        @NotNull List<String> trainerUsernames
) {}
