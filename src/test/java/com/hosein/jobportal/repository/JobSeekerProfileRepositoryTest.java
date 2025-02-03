package com.hosein.jobportal.repository;

import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.entity.Skill;
import com.hosein.jobportal.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobSeekerProfileRepositoryTest {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    private JobSeekerProfile testProfile;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create User first
        testUser = User.builder()
                .email("jobseeker" + System.currentTimeMillis() + "@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        testUser = userRepository.save(testUser);

        // Create JobSeekerProfile
        testProfile = JobSeekerProfile.builder()
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .workAuthorization("Citizen")
                .employmentType("Full-time")
                .resume("resume.pdf")
                .build();
    }

    @Test
    @DisplayName("Should save job seeker profile successfully")
    void shouldSaveJobSeekerProfileSuccessfully() {
        // Act
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Assert
        assertNotNull(savedProfile);
        assertEquals(testUser.getUserId(), savedProfile.getUserAccountId());
        assertEquals(testProfile.getFirstName(), savedProfile.getFirstName());
        assertEquals(testProfile.getLastName(), savedProfile.getLastName());
    }

    @Test
    @DisplayName("Should find job seeker profile by ID")
    void shouldFindJobSeekerProfileById() {
        // Arrange
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Act
        Optional<JobSeekerProfile> foundProfile = jobSeekerProfileRepository.findById(savedProfile.getUserAccountId());

        // Assert
        assertTrue(foundProfile.isPresent());
        assertEquals(savedProfile.getUserAccountId(), foundProfile.get().getUserAccountId());
        assertEquals(savedProfile.getFirstName(), foundProfile.get().getFirstName());
    }

    @Test
    @DisplayName("Should update job seeker profile successfully")
    void shouldUpdateJobSeekerProfileSuccessfully() {
        // Arrange
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);
        savedProfile.setFirstName("Updated Name");
        savedProfile.setCity("Updated City");

        // Act
        JobSeekerProfile updatedProfile = jobSeekerProfileRepository.save(savedProfile);

        // Assert
        assertEquals("Updated Name", updatedProfile.getFirstName());
        assertEquals("Updated City", updatedProfile.getCity());
        assertEquals(savedProfile.getUserAccountId(), updatedProfile.getUserAccountId());
    }

    @Test
    @DisplayName("Should delete job seeker profile")
    void shouldDeleteJobSeekerProfile() {
        // Arrange
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Act
        jobSeekerProfileRepository.delete(savedProfile);
        Optional<JobSeekerProfile> deletedProfile = jobSeekerProfileRepository.findById(savedProfile.getUserAccountId());

        // Assert
        assertTrue(deletedProfile.isEmpty());
    }

    @Test
    @DisplayName("Should save job seeker profile with skills")
    void shouldSaveJobSeekerProfileWithSkills() {
        // Arrange
        Skill skill1 = Skill.builder()
                .name("Java")
                .experienceLevel("Expert")
                .yearsOfExperience("5")
                .build();

        Skill skill2 = Skill.builder()
                .name("Spring Boot")
                .experienceLevel("Intermediate")
                .yearsOfExperience("3")
                .build();

        testProfile.setSkills(Arrays.asList(skill1, skill2));
        skill1.setJobSeekerProfile(testProfile);
        skill2.setJobSeekerProfile(testProfile);

        // Act
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Assert
        assertNotNull(savedProfile.getSkills());
        assertEquals(2, savedProfile.getSkills().size());
        assertTrue(savedProfile.getSkills().stream()
                .map(Skill::getName)
                .anyMatch(name -> name.equals("Java")));
        assertTrue(savedProfile.getSkills().stream()
                .map(Skill::getName)
                .anyMatch(name -> name.equals("Spring Boot")));
    }

    @Test
    @DisplayName("Should get photos image path")
    void shouldGetPhotosImagePath() {
        // Arrange
        testProfile.setProfilePhoto("photo.jpg");
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Act
        String photoPath = savedProfile.getPhotosImagePath();

        // Assert
        assertNotNull(photoPath);
        assertTrue(photoPath.contains("/photos/candidate/"));
        assertTrue(photoPath.endsWith("/photo.jpg"));
    }

    @Test
    @DisplayName("Should return null for photos image path when no photo")
    void shouldReturnNullForPhotosImagePathWhenNoPhoto() {
        // Arrange
        JobSeekerProfile savedProfile = jobSeekerProfileRepository.save(testProfile);

        // Act
        String photoPath = savedProfile.getPhotosImagePath();

        // Assert
        assertNull(photoPath);
    }
} 