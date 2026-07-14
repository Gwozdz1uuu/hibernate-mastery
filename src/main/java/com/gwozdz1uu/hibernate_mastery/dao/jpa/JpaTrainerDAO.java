package com.gwozdz1uu.hibernate_mastery.dao.jpa;

import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTrainerDAO implements TrainerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Trainer save(Trainer trainer) {
        em.persist(trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return em.merge(trainer);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return em.createQuery(
                        "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "SELECT COUNT(t) FROM Trainer t WHERE t.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Trainer> findUnassignedToTrainee(String traineeUsername) {
        return em.createQuery(
                        "SELECT t FROM Trainer t WHERE t NOT IN " +
                                "(SELECT tr FROM Trainee tn JOIN tn.trainers tr WHERE tn.username = :username)",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }
}
