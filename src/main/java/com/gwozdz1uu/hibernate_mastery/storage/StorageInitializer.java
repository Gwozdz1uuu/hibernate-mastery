package com.epam.gym_crm_system.storage;

import com.epam.gym_crm_system.model.Trainee;
import com.epam.gym_crm_system.model.Trainer;
import com.epam.gym_crm_system.model.Training;
import com.epam.gym_crm_system.model.TrainingType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageInitializer {

    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Training> trainingStorage;

    @Value("${storage.initialdata.filepath}")
    private String initialDataFilePath;

    @Autowired
    public StorageInitializer(Map<Long, Trainee> traineeStorage,
                              Map<Long, Trainer> trainerStorage,
                              Map<Long, Training> trainingStorage) {
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing storage from file: {}", initialDataFilePath);
        try {
            ClassPathResource resource = new ClassPathResource(initialDataFilePath);
            InputStream inputStream = resource.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            JsonNode root = mapper.readTree(inputStream);

            loadTrainers(root);
            loadTrainees(root);
            loadTrainings(root);

            logger.info("Storage initialization completed. Trainers: {}, Trainees: {}, Trainings: {}",
                    trainerStorage.size(), traineeStorage.size(), trainingStorage.size());
        } catch (IOException e) {
            logger.error("Failed to initialize storage from file: {}", initialDataFilePath, e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    private void loadTrainers(JsonNode root) {
        JsonNode trainersNode = root.get("trainers");
        if (trainersNode != null && trainersNode.isArray()) {
            for (JsonNode node : trainersNode) {
                Trainer trainer = new Trainer();
                trainer.setId(node.get("id").asLong());
                trainer.setFirstName(node.get("firstName").asText());
                trainer.setLastName(node.get("lastName").asText());
                trainer.setUsername(node.get("username").asText());
                trainer.setPassword(node.get("password").asText());
                trainer.setIsActive(node.get("isActive").asBoolean());
                trainer.setSpecialization(node.get("specialization").asText());
                trainerStorage.put(trainer.getId(), trainer);
                logger.debug("Loaded trainer: {}", trainer.getUsername());
            }
        }
    }

    private void loadTrainees(JsonNode root) {
        JsonNode traineesNode = root.get("trainees");
        if (traineesNode != null && traineesNode.isArray()) {
            for (JsonNode node : traineesNode) {
                Trainee trainee = new Trainee();
                trainee.setId(node.get("id").asLong());
                trainee.setFirstName(node.get("firstName").asText());
                trainee.setLastName(node.get("lastName").asText());
                trainee.setUsername(node.get("username").asText());
                trainee.setPassword(node.get("password").asText());
                trainee.setIsActive(node.get("isActive").asBoolean());
                if (node.has("dateOfBirth") && !node.get("dateOfBirth").isNull()) {
                    trainee.setDateOfBirth(LocalDate.parse(node.get("dateOfBirth").asText()));
                }
                if (node.has("address") && !node.get("address").isNull()) {
                    trainee.setAddress(node.get("address").asText());
                }
                traineeStorage.put(trainee.getId(), trainee);
                logger.debug("Loaded trainee: {}", trainee.getUsername());
            }
        }
    }

    private void loadTrainings(JsonNode root) {
        JsonNode trainingsNode = root.get("trainings");
        if (trainingsNode != null && trainingsNode.isArray()) {
            for (JsonNode node : trainingsNode) {
                Training training = new Training();
                training.setId(node.get("id").asLong());
                training.setTraineeId(node.get("traineeId").asLong());
                training.setTrainerId(node.get("trainerId").asLong());
                training.setTrainingName(node.get("trainingName").asText());
                training.setTrainingType(new TrainingType(node.get("trainingType").asText()));
                training.setTrainingDate(LocalDate.parse(node.get("trainingDate").asText()));
                training.setTrainingDuration(node.get("trainingDuration").asInt());
                trainingStorage.put(training.getId(), training);
                logger.debug("Loaded training: {}", training.getTrainingName());
            }
        }
    }
}
