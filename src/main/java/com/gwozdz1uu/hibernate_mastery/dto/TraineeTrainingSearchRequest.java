package com.gwozdz1uu.hibernate_mastery.dto;

import java.time.LocalDate;

public record TraineeTrainingSearchRequest(
        String username,
        String password,
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        String trainingTypeName
) {}
