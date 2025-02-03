package se.jobportal.repository;

import se.jobportal.entity.RecruiterProfile;
import se.jobportal.entity.User;
import se.jobportal.entity.UserType;
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
class RecruiterProfileRepositoryTest {

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    private RecruiterProfile testRecruiterProfile;
    private User testUser;
    private UserType recruiterType;

    @BeforeEach
    void setUp() {
        // Create UserType for recruiter
        recruiterType = UserType.builder()
                .userTypeName("RECRUITER_" + System.currentTimeMillis())
                .build();
        recruiterType = userTypeRepository.save(recruiterType);

        // Create User
        testUser = User.builder()
                .email("recruiter" + System.currentTimeMillis() + "@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .userType(recruiterType)
                .build();
        testUser = userRepository.save(testUser);

        // Create RecruiterProfile
        testRecruiterProfile = RecruiterProfile.builder()
                .user(testUser)
                .firstName("John")
                .lastName("Recruiter")
                .company("Test Company")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .build();
    }

    @Test
    @DisplayName("Should save recruiter profile successfully")
    void shouldSaveRecruiterProfileSuccessfully() {
        // Act
        RecruiterProfile savedProfile = recruiterProfileRepository.save(testRecruiterProfile);

        // Assert
        assertNotNull(savedProfile);
        assertEquals(testUser.getUserId(), savedProfile.getUserAccountId());
        assertEquals(testRecruiterProfile.getFirstName(), savedProfile.getFirstName());
        assertEquals(testRecruiterProfile.getCompany(), savedProfile.getCompany());
    }

    @Test
    @DisplayName("Should find recruiter profile by ID")
    void shouldFindRecruiterProfileById() {
        // Arrange
        RecruiterProfile savedProfile = recruiterProfileRepository.save(testRecruiterProfile);

        // Act
        Optional<RecruiterProfile> foundProfile = recruiterProfileRepository.findById(savedProfile.getUserAccountId());

        // Assert
        assertTrue(foundProfile.isPresent());
        assertEquals(savedProfile.getUserAccountId(), foundProfile.get().getUserAccountId());
        assertEquals(savedProfile.getFirstName(), foundProfile.get().getFirstName());
    }

    @Test
    @DisplayName("Should not find recruiter profile with non-existent ID")
    void shouldNotFindRecruiterProfileWithNonExistentId() {
        // Act
        Optional<RecruiterProfile> foundProfile = recruiterProfileRepository.findById(999999);

        // Assert
        assertTrue(foundProfile.isEmpty());
    }

    @Test
    @DisplayName("Should update recruiter profile successfully")
    void shouldUpdateRecruiterProfileSuccessfully() {
        // Arrange
        RecruiterProfile savedProfile = recruiterProfileRepository.save(testRecruiterProfile);
        savedProfile.setFirstName("Updated Name");
        savedProfile.setCompany("Updated Company");

        // Act
        RecruiterProfile updatedProfile = recruiterProfileRepository.save(savedProfile);

        // Assert
        assertEquals("Updated Name", updatedProfile.getFirstName());
        assertEquals("Updated Company", updatedProfile.getCompany());
        assertEquals(savedProfile.getUserAccountId(), updatedProfile.getUserAccountId());
    }

    @Test
    @DisplayName("Should delete recruiter profile successfully")
    void shouldDeleteRecruiterProfileSuccessfully() {
        // Arrange
        RecruiterProfile savedProfile = recruiterProfileRepository.save(testRecruiterProfile);

        // Act
        recruiterProfileRepository.delete(savedProfile);
        Optional<RecruiterProfile> deletedProfile = recruiterProfileRepository.findById(savedProfile.getUserAccountId());

        // Assert
        assertTrue(deletedProfile.isEmpty());
    }

    @Test
    @DisplayName("Should find all recruiter profiles")
    void shouldFindAllRecruiterProfiles() {
        // Arrange
        recruiterProfileRepository.save(testRecruiterProfile);

        // Create another recruiter profile
        User anotherUser = User.builder()
                .email("another.recruiter" + System.currentTimeMillis() + "@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .userType(recruiterType)
                .build();
        anotherUser = userRepository.save(anotherUser);

        RecruiterProfile anotherProfile = RecruiterProfile.builder()
                .user(anotherUser)
                .firstName("Jane")
                .lastName("Recruiter")
                .company("Another Company")
                .city("Another City")
                .state("Another State")
                .country("Another Country")
                .build();
        recruiterProfileRepository.save(anotherProfile);

        // Act
        List<RecruiterProfile> profiles = recruiterProfileRepository.findAll();

        // Assert
        assertFalse(profiles.isEmpty());
        assertTrue(profiles.size() >= 2);
    }
} 