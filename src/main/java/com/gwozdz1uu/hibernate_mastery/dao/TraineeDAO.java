package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDAO {

    @PersistenceContext
    private EntityManager em;


    public Trainee create(Trainee trainee) {
        em.persist(trainee);
        return trainee;
    }

    public Trainee update(Trainee trainee) {
        return em.merge(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        TypedQuery<Trainee> query = em.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(trainee -> em.remove(em.contains(trainee) ? trainee : em.merge(trainee)));
    }

    public List<Trainee> findAll() {
        return em.createQuery("SELECT t FROM Trainee t", Trainee.class).getResultList();
    }
}
