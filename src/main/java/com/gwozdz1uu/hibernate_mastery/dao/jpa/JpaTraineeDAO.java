package com.gwozdz1uu.hibernate_mastery.dao.jpa;

import com.gwozdz1uu.hibernate_mastery.dao.TraineeRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTraineeDAO implements TraineeRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Trainee save(Trainee trainee) {
        em.persist(trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return em.merge(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        TypedQuery<Trainee> query = em.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    @Override
    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(trainee -> em.remove(em.contains(trainee) ? trainee : em.merge(trainee)));
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}
