package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAO {

    @PersistenceContext
    private EntityManager em;


    public Trainer create(Trainer trainer) {
        em.persist(trainer);
        return trainer;
    }

    public Trainer update(Trainer trainer) {
        em.merge(trainer);
        return trainer;
    }

    public Optional<Trainer> findByUsername(String username) {
        return em.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream().findFirst();
    }

    public List<Trainer> findAll() {
        return em.createQuery("SELECT t FROM Trainer t", Trainer.class).getResultList();
    }

    public List<Trainer> findUnassignedToTrainee(String traineeUsername) {
        return em.createQuery(
                        "SELECT t FROM Trainer t WHERE t NOT IN " +
                                "(SELECT tr FROM Trainee tn JOIN tn.trainers tr WHERE tn.username = :username)",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }
}
