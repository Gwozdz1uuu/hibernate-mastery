package com.gwozdz1uu.hibernate_mastery.dao.jpa;

import java.time.LocalDate;

record TrainingSearchCriteria(
        String ownerUsername,
        OwnerType ownerType,
        LocalDate fromDate,
        LocalDate toDate,
        String personName,
        String trainingTypeName
) {
    enum OwnerType {
        TRAINEE, TRAINER
    }
}
