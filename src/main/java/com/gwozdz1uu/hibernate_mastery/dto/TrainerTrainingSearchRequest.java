package com.gwozdz1uu.hibernate_mastery.dto;

import java.time.LocalDate;

public record TrainerTrainingSearchRequest(
        String username,
        String password,
        LocalDate fromDate,
        LocalDate toDate,
        String traineeName
) {}
