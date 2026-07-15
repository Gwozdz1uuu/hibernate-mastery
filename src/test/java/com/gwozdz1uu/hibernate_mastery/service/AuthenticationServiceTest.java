package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TraineeRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticateTrainee_validCredentials_returnsTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("encoded");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);

        Trainee result = authenticationService.authenticateTrainee("john.doe", "secret");

        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void authenticateTrainee_wrongPassword_throws() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("encoded");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticateTrainee("john.doe", "wrong"));
    }

    @Test
    void authenticateTrainee_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> authenticationService.authenticateTrainee("missing", "secret"));
    }

    @Test
    void authenticateTrainer_validCredentials_returnsTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("coach.one");
        trainer.setPassword("encoded");
        when(trainerRepository.findByUsername("coach.one")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);

        Trainer result = authenticationService.authenticateTrainer("coach.one", "secret");

        assertEquals("coach.one", result.getUsername());
    }

    @Test
    void traineeCredentialsMatch_returnsFalseWhenNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertFalse(authenticationService.traineeCredentialsMatch("missing", "secret"));
    }
}
