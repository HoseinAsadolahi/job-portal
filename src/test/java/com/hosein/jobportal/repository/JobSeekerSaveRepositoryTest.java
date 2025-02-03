package com.hosein.jobportal.repository;

import com.hosein.jobportal.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobSeekerSaveRepositoryTest {

    @Autowired
    private JobSeekerSaveRepository jobSeekerSaveRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    private UserRepository userRepository;

    private JobSeekerProfile jobSeeker;
    private JobPostActivity jobPost;
    private JobSeekerSave jobSeekerSave;
    private User user;

    @BeforeEach
    void setUp() {
        // Create User first
        user = User.builder()
                .email("john.doe" + System.currentTimeMillis() + "@example.com")
                .password("password")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        user = userRepository.save(user);

        // Create JobSeekerProfile with User
        jobSeeker = JobSeekerProfile.builder()
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .build();
        jobSeeker = jobSeekerProfileRepository.save(jobSeeker);

        // Create JobPostActivity
        jobPost = JobPostActivity.builder()
                .jobTitle("Software Engineer")
                .descriptionOfJob("Java Developer position")
                .remote("Remote")
                .jobType("Full-time")
                .postedDate(new Date())
                .build();
        jobPost = jobPostActivityRepository.save(jobPost);

        // Create JobSeekerSave
        jobSeekerSave = JobSeekerSave.builder()
                .userId(jobSeeker)
                .job(jobPost)
                .build();
    }

    @Test
    @DisplayName("Should save job successfully")
    void shouldSaveJob() {
        // Act
        JobSeekerSave savedJob = jobSeekerSaveRepository.save(jobSeekerSave);

        // Assert
        assertNotNull(savedJob.getId());
        assertEquals(jobSeeker.getUserAccountId(), savedJob.getUserId().getUserAccountId());
        assertEquals(jobPost.getJobPostId(), savedJob.getJob().getJobPostId());
    }

    @Test
    @DisplayName("Should find saved jobs by user ID")
    void shouldFindSavedJobsByUserId() {
        // Arrange
        jobSeekerSaveRepository.save(jobSeekerSave);
        
        JobPostActivity anotherJob = JobPostActivity.builder()
                .jobTitle("Senior Developer")
                .descriptionOfJob("Senior position")
                .remote("Hybrid")
                .jobType("Full-time")
                .postedDate(new Date())
                .build();
        anotherJob = jobPostActivityRepository.save(anotherJob);

        JobSeekerSave anotherSave = JobSeekerSave.builder()
                .userId(jobSeeker)
                .job(anotherJob)
                .build();
        jobSeekerSaveRepository.save(anotherSave);

        // Act
        List<JobSeekerSave> savedJobs = jobSeekerSaveRepository.findByUserId(jobSeeker);

        // Assert
        assertEquals(2, savedJobs.size());
        assertTrue(savedJobs.stream()
                .allMatch(save -> save.getUserId().getUserAccountId().equals(jobSeeker.getUserAccountId())));
    }

    @Test
    @DisplayName("Should find all saves for a job post")
    void shouldFindAllSavesForJob() {
        // Arrange
        jobSeekerSaveRepository.save(jobSeekerSave);
        
        // Create another user and job seeker
        User anotherUser = User.builder()
                .email("jane.smith" + System.currentTimeMillis() + "@example.com")
                .password("password")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        anotherUser = userRepository.save(anotherUser);

        JobSeekerProfile anotherJobSeeker = JobSeekerProfile.builder()
                .user(anotherUser)
                .firstName("Jane")
                .lastName("Smith")
                .city("Another City")
                .state("Another State")
                .country("Another Country")
                .build();
        anotherJobSeeker = jobSeekerProfileRepository.save(anotherJobSeeker);

        JobSeekerSave anotherSave = JobSeekerSave.builder()
                .userId(anotherJobSeeker)
                .job(jobPost)
                .build();
        jobSeekerSaveRepository.save(anotherSave);

        // Act
        List<JobSeekerSave> saves = jobSeekerSaveRepository.findByJob(jobPost);

        // Assert
        assertEquals(2, saves.size());
        assertTrue(saves.stream()
                .allMatch(save -> save.getJob().getJobPostId().equals(jobPost.getJobPostId())));
    }

    @Test
    @DisplayName("Should enforce unique constraint on userId and job combination")
    void shouldEnforceUniqueConstraint() {
        // Arrange
        jobSeekerSaveRepository.save(jobSeekerSave);

        // Create duplicate save
        JobSeekerSave duplicateSave = JobSeekerSave.builder()
                .userId(jobSeeker)
                .job(jobPost)
                .build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            jobSeekerSaveRepository.save(duplicateSave);
            jobSeekerSaveRepository.flush();
        });
    }

    @Test
    @DisplayName("Should delete all saves for a job")
    void shouldDeleteAllSavesForJob() {
        // Arrange
        jobSeekerSaveRepository.save(jobSeekerSave);
        
        // Create another save for the same job
        User anotherUser = User.builder()
                .email("another" + System.currentTimeMillis() + "@example.com")
                .password("password")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        anotherUser = userRepository.save(anotherUser);

        JobSeekerProfile anotherJobSeeker = JobSeekerProfile.builder()
                .user(anotherUser)
                .firstName("Another")
                .lastName("User")
                .city("City")
                .state("State")
                .country("Country")
                .build();
        anotherJobSeeker = jobSeekerProfileRepository.save(anotherJobSeeker);

        JobSeekerSave anotherSave = JobSeekerSave.builder()
                .userId(anotherJobSeeker)
                .job(jobPost)
                .build();
        jobSeekerSaveRepository.save(anotherSave);

        // Act
        jobSeekerSaveRepository.deleteAllByJob(jobPost);
        List<JobSeekerSave> remainingSaves = jobSeekerSaveRepository.findByJob(jobPost);

        // Assert
        assertTrue(remainingSaves.isEmpty());
    }
} 