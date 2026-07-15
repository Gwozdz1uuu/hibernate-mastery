package com.gwozdz1uu.hibernate_mastery.dao.jpa;

import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class JpaTrainingDAO implements TrainingRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Training save(Training training) {
        em.persist(training);
        return training;
    }

    @Override
    public List<Training> findByTraineeCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        return findByCriteria(new TrainingSearchCriteria(
                traineeUsername,
                TrainingSearchCriteria.OwnerType.TRAINEE,
                fromDate,
                toDate,
                trainerName,
                trainingTypeName
        ));
    }

    @Override
    public List<Training> findByTrainerCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return findByCriteria(new TrainingSearchCriteria(
                trainerUsername,
                TrainingSearchCriteria.OwnerType.TRAINER,
                fromDate,
                toDate,
                traineeName,
                null
        ));
    }

    private List<Training> findByCriteria(TrainingSearchCriteria criteria) {
        String ownerField = criteria.ownerType() == TrainingSearchCriteria.OwnerType.TRAINEE
                ? "tr.trainee.username"
                : "tr.trainer.username";

        StringBuilder jpql = new StringBuilder("SELECT tr FROM Training tr WHERE ")
                .append(ownerField)
                .append(" = :username");

        if (criteria.fromDate() != null) jpql.append(" AND tr.trainingDate >= :fromDate");
        if (criteria.toDate() != null) jpql.append(" AND tr.trainingDate <= :toDate");

        if (criteria.personName() != null) {
            String personPrefix = criteria.ownerType() == TrainingSearchCriteria.OwnerType.TRAINEE
                    ? "tr.trainer"
                    : "tr.trainee";
            jpql.append(" AND (").append(personPrefix).append(".firstName LIKE :personName")
                    .append(" OR ").append(personPrefix).append(".lastName LIKE :personName)");
        }

        if (criteria.trainingTypeName() != null) {
            jpql.append(" AND tr.trainingType.trainingTypeName = :typeName");
        }

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);
        query.setParameter("username", criteria.ownerUsername());
        if (criteria.fromDate() != null) query.setParameter("fromDate", criteria.fromDate());
        if (criteria.toDate() != null) query.setParameter("toDate", criteria.toDate());
        if (criteria.personName() != null) query.setParameter("personName", "%" + criteria.personName() + "%");
        if (criteria.trainingTypeName() != null) query.setParameter("typeName", criteria.trainingTypeName());

        return query.getResultList();
    }
}
