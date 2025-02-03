package com.hosein.jobportal.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerApply;
import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobSeekerApplyRepositoryTest {

    @Autowired
    private JobSeekerApplyRepository jobSeekerApplyRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    private UserRepository userRepository;

    private JobSeekerProfile jobSeeker;
    private JobPostActivity jobPost;
    private JobSeekerApply jobSeekerApply;
    private User user;

    @BeforeEach
    void setUp() {
        // Create User first
        user = User.builder()
                .email("john.doe@example.com")
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

        // Create JobPostActivity with minimum required fields
        jobPost = JobPostActivity.builder()
                .jobTitle("Software Engineer")
                .descriptionOfJob("Java Developer position")
                .remote("Remote")
                .jobType("Full-time")
                .postedDate(new Date())
                .build();
        jobPost = jobPostActivityRepository.save(jobPost);

        // Create JobSeekerApply
        jobSeekerApply = JobSeekerApply.builder()
                .userId(jobSeeker)
                .job(jobPost)
                .applyDate(new Date())
                .coverLetter("I am interested in this position")
                .build();
    }

    @Test
    @DisplayName("Should save job application successfully")
    void shouldSaveJobApplication() {
        // Act
        JobSeekerApply savedApplication = jobSeekerApplyRepository.save(jobSeekerApply);

        // Assert
        assertNotNull(savedApplication.getId());
        assertEquals(jobSeeker.getUserAccountId(), savedApplication.getUserId().getUserAccountId());
        assertEquals(jobPost.getJobPostId(), savedApplication.getJob().getJobPostId());
        assertNotNull(savedApplication.getApplyDate());
        assertEquals("I am interested in this position", savedApplication.getCoverLetter());
    }

    @Test
    @DisplayName("Should find job application by ID")
    void shouldFindJobApplicationById() {
        // Arrange
        JobSeekerApply savedApplication = jobSeekerApplyRepository.save(jobSeekerApply);

        // Act
        Optional<JobSeekerApply> foundApplication = jobSeekerApplyRepository.findById(savedApplication.getId());

        // Assert
        assertTrue(foundApplication.isPresent());
        assertEquals(savedApplication.getId(), foundApplication.get().getId());
        assertEquals(savedApplication.getCoverLetter(), foundApplication.get().getCoverLetter());
    }

    @Test
    @DisplayName("Should find all applications by job seeker")
    void shouldFindAllApplicationsByJobSeeker() {
        // Arrange
        jobSeekerApplyRepository.save(jobSeekerApply);
        
        // Create and save another application for the same job seeker
        JobPostActivity anotherJob = JobPostActivity.builder()
                .jobTitle("Senior Developer")
                .descriptionOfJob("Senior Java Developer position")
                .remote("On-site")
                .jobType("Full-time")
                .postedDate(new Date())
                .build();
        anotherJob = jobPostActivityRepository.save(anotherJob);

        JobSeekerApply anotherApplication = JobSeekerApply.builder()
                .userId(jobSeeker)
                .job(anotherJob)
                .applyDate(new Date())
                .coverLetter("Interested in senior position")
                .build();
        jobSeekerApplyRepository.save(anotherApplication);

        // Act
        List<JobSeekerApply> applications = jobSeekerApplyRepository.findByUserId(jobSeeker);

        // Assert
        assertEquals(2, applications.size());
        assertTrue(applications.stream()
                .allMatch(app -> app.getUserId().getUserAccountId().equals(jobSeeker.getUserAccountId())));
    }

    @Test
    @DisplayName("Should find all applications for a job post")
    void shouldFindAllApplicationsForJob() {
        // Arrange
        jobSeekerApplyRepository.save(jobSeekerApply);
        
        // Create another user and job seeker
        User anotherUser = User.builder()
                .email("jane.smith@example.com")
                .password("password")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        anotherUser = userRepository.save(anotherUser);

        // Create another job seeker profile
        JobSeekerProfile anotherJobSeeker = JobSeekerProfile.builder()
                .user(anotherUser)
                .firstName("Jane")
                .lastName("Smith")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .build();
        anotherJobSeeker = jobSeekerProfileRepository.save(anotherJobSeeker);

        JobSeekerApply anotherApplication = JobSeekerApply.builder()
                .userId(anotherJobSeeker)
                .job(jobPost)
                .applyDate(new Date())
                .coverLetter("Another application")
                .build();
        jobSeekerApplyRepository.save(anotherApplication);

        // Act
        List<JobSeekerApply> applications = jobSeekerApplyRepository.findByJob(jobPost);

        // Assert
        assertEquals(2, applications.size());
        assertTrue(applications.stream()
                .allMatch(app -> app.getJob().getJobPostId().equals(jobPost.getJobPostId())));
    }

    @Test
    @DisplayName("Should enforce unique constraint on userId and job combination")
    void shouldEnforceUniqueConstraint() {
        // Arrange
        jobSeekerApplyRepository.save(jobSeekerApply);

        // Create duplicate application
        JobSeekerApply duplicateApplication = JobSeekerApply.builder()
                .userId(jobSeeker)
                .job(jobPost)
                .applyDate(new Date())
                .coverLetter("Duplicate application")
                .build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            jobSeekerApplyRepository.save(duplicateApplication);
            jobSeekerApplyRepository.flush();
        });
    }

    @Test
    @DisplayName("Should delete job application")
    void shouldDeleteJobApplication() {
        // Arrange
        JobSeekerApply savedApplication = jobSeekerApplyRepository.save(jobSeekerApply);

        // Act
        jobSeekerApplyRepository.delete(savedApplication);
        Optional<JobSeekerApply> deletedApplication = jobSeekerApplyRepository.findById(savedApplication.getId());

        // Assert
        assertTrue(deletedApplication.isEmpty());
    }

    @Test
    @DisplayName("Should update job application")
    void shouldUpdateJobApplication() {
        // Arrange
        JobSeekerApply savedApplication = jobSeekerApplyRepository.save(jobSeekerApply);
        String updatedCoverLetter = "Updated cover letter";
        savedApplication.setCoverLetter(updatedCoverLetter);

        // Act
        JobSeekerApply updatedApplication = jobSeekerApplyRepository.save(savedApplication);

        // Assert
        assertEquals(updatedCoverLetter, updatedApplication.getCoverLetter());
        assertEquals(savedApplication.getId(), updatedApplication.getId());
    }
} 