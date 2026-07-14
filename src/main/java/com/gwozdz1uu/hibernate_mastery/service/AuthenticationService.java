package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TraineeRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public Trainee authenticateTrainee(String username, String password) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        if (!passwordMatches(trainee.getPassword(), password)) {
            throw new AuthenticationException("Invalid password for: " + username);
        }
        return trainee;
    }

    public Trainer authenticateTrainer(String username, String password) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        if (!passwordMatches(trainer.getPassword(), password)) {
            throw new AuthenticationException("Invalid password for: " + username);
        }
        return trainer;
    }

    public boolean traineeCredentialsMatch(String username, String password) {
        return traineeRepository.findByUsername(username)
                .map(t -> passwordMatches(t.getPassword(), password))
                .orElse(false);
    }

    public boolean trainerCredentialsMatch(String username, String password) {
        return trainerRepository.findByUsername(username)
                .map(t -> passwordMatches(t.getPassword(), password))
                .orElse(false);
    }

    private boolean passwordMatches(String stored, String provided) {
        return passwordEncoder.matches(provided, stored);
    }
}
