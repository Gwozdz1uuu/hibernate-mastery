package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TrainingDAO {

    @PersistenceContext
    private EntityManager em;

    public Training create(Training training) {
        em.persist(training);
        return training;
    }

    public List<Training> findByTraineeCriteria(
            String traineeUsername, LocalDate fromDate, LocalDate toDate,
            String trainerName, String trainingTypeName) {
        StringBuilder jpql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainee.username = :username");
        if (fromDate != null) jpql.append(" AND tr.trainingDate >= :fromDate");
        if (toDate != null)   jpql.append(" AND tr.trainingDate <= :toDate");
        if (trainerName != null) {
            jpql.append(" AND (tr.trainer.firstName LIKE :trainerName")
                .append(" OR tr.trainer.lastName LIKE :trainerName)");
        }
        if (trainingTypeName != null) jpql.append(" AND tr.trainingType.trainingTypeName = :typeName");

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);
        query.setParameter("username", traineeUsername);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null)   query.setParameter("toDate", toDate);
        if (trainerName != null) query.setParameter("trainerName", "%" + trainerName + "%");
        if (trainingTypeName != null) query.setParameter("typeName", trainingTypeName);

        return query.getResultList();
    }

    public List<Training> findByTrainerCriteria(
            String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        StringBuilder jpql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainer.username = :username");
        if (fromDate != null) jpql.append(" AND tr.trainingDate >= :fromDate");
        if (toDate != null)   jpql.append(" AND tr.trainingDate <= :toDate");
        if (traineeName != null) {
            jpql.append(" AND (tr.trainee.firstName LIKE :traineeName")
                .append(" OR tr.trainee.lastName LIKE :traineeName)");
        }
        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);
        query.setParameter("username", trainerUsername);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null)   query.setParameter("toDate", toDate);
        if (traineeName != null) query.setParameter("traineeName", "%" + traineeName + "%");
        return query.getResultList();
    }
}
