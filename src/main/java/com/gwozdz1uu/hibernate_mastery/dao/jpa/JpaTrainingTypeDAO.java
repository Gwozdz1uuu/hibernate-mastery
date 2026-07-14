package com.gwozdz1uu.hibernate_mastery.dao.jpa;

import com.gwozdz1uu.hibernate_mastery.dao.TrainingTypeRepository;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTrainingTypeDAO implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(em.find(TrainingType.class, id));
    }
}
