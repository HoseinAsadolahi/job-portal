package com.hosein.jobportal.repository;

import com.hosein.jobportal.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobPostActivityRepositoryTest {

    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    private UserRepository userRepository;

    private JobPostActivity testJobPost;
    private JobLocation jobLocation;
    private JobCompany jobCompany;
    private User recruiter;

    @BeforeEach
    void setUp() {
        // Create JobLocation
        jobLocation = JobLocation.builder()
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .build();

        // Create JobCompany
        jobCompany = JobCompany.builder()
                .name("Test Company")
                .build();

        // Create Recruiter
        recruiter = User.builder()
                .email("recruiter" + System.currentTimeMillis() + "@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        recruiter = userRepository.save(recruiter);

        // Create JobPostActivity with cascading entities
        testJobPost = JobPostActivity.builder()
                .jobTitle("Software Engineer")
                .descriptionOfJob("Test job description")
                .remote("Remote")
                .jobType("Full-time")
                .salary("100K-120K")
                .postedDate(new Date())
                .jobLocationId(jobLocation)
                .jobCompanyId(jobCompany)
                .postedById(recruiter)
                .build();
    }

    @Test
    @DisplayName("Should save job post successfully")
    void shouldSaveJobPost() {
        // Act
        JobPostActivity savedJobPost = jobPostActivityRepository.save(testJobPost);

        // Assert
        assertNotNull(savedJobPost);
        assertNotNull(savedJobPost.getJobPostId());
        assertEquals(testJobPost.getJobTitle(), savedJobPost.getJobTitle());
        assertNotNull(savedJobPost.getJobLocationId().getId());
        assertNotNull(savedJobPost.getJobCompanyId().getId());
    }

    @Test
    @DisplayName("Should find job post by ID")
    void shouldFindJobPostById() {
        // Arrange
        JobPostActivity savedJobPost = jobPostActivityRepository.save(testJobPost);

        // Act
        Optional<JobPostActivity> foundJobPost = jobPostActivityRepository.findById(savedJobPost.getJobPostId());

        // Assert
        assertTrue(foundJobPost.isPresent());
        assertEquals(savedJobPost.getJobPostId(), foundJobPost.get().getJobPostId());
        assertEquals(savedJobPost.getJobTitle(), foundJobPost.get().getJobTitle());
    }

    @Test
    @DisplayName("Should get recruiter jobs")
    void shouldGetRecruiterJobs() {
        // Arrange
        JobPostActivity savedJobPost = jobPostActivityRepository.save(testJobPost);

        // Create another job post
        JobPostActivity anotherJobPost = JobPostActivity.builder()
                .jobTitle("Senior Developer")
                .descriptionOfJob("Senior position")
                .remote("Hybrid")
                .jobType("Full-time")
                .salary("130K-150K")
                .postedDate(new Date())
                .jobLocationId(jobLocation)
                .jobCompanyId(jobCompany)
                .postedById(recruiter)
                .build();
        jobPostActivityRepository.save(anotherJobPost);

        // Act
        List<IRecruiterJobs> recruiterJobs = jobPostActivityRepository.getRecruiterJobs(recruiter.getUserId());

        // Assert
        assertFalse(recruiterJobs.isEmpty());
        assertEquals(2, recruiterJobs.size());
        recruiterJobs.forEach(job -> {
            assertNotNull(job.getJob_post_id());
            assertNotNull(job.getJob_title());
            assertNotNull(job.getLocationId());
            assertNotNull(job.getCompanyId());
        });
    }

    @Test
    @DisplayName("Should delete job post")
    void shouldDeleteJobPost() {
        // Arrange
        JobPostActivity savedJobPost = jobPostActivityRepository.save(testJobPost);

        // Act
        jobPostActivityRepository.deleteById(savedJobPost.getJobPostId());
        Optional<JobPostActivity> deletedJobPost = jobPostActivityRepository.findById(savedJobPost.getJobPostId());

        // Assert
        assertTrue(deletedJobPost.isEmpty());
    }

    @Test
    @DisplayName("Should update job post")
    void shouldUpdateJobPost() {
        // Arrange
        JobPostActivity savedJobPost = jobPostActivityRepository.save(testJobPost);
        String updatedTitle = "Updated Job Title";
        savedJobPost.setJobTitle(updatedTitle);

        // Act
        JobPostActivity updatedJobPost = jobPostActivityRepository.save(savedJobPost);

        // Assert
        assertEquals(updatedTitle, updatedJobPost.getJobTitle());
        assertEquals(savedJobPost.getJobPostId(), updatedJobPost.getJobPostId());
    }
} 