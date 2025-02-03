package se.jobportal.repository;

import se.jobportal.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserTypeRepositoryTest {

    @Autowired
    private UserTypeRepository userTypeRepository;

    private UserType testUserType;

    @BeforeEach
    void setUp() {
        testUserType = UserType.builder()
                .userTypeName("JOB_SEEKER")
                .build();
    }

    @Test
    @DisplayName("Should save user type successfully")
    void shouldSaveUserTypeSuccessfully() {
        // Act
        UserType savedUserType = userTypeRepository.save(testUserType);

        // Assert
        assertNotNull(savedUserType);
        assertTrue(savedUserType.getUserTypeId() > 0);
        assertEquals(testUserType.getUserTypeName(), savedUserType.getUserTypeName());
    }

    @Test
    @DisplayName("Should find user type by ID")
    void shouldFindUserTypeById() {
        // Arrange
        UserType savedUserType = userTypeRepository.save(testUserType);

        // Act
        Optional<UserType> foundUserType = userTypeRepository.findById(savedUserType.getUserTypeId());

        // Assert
        assertTrue(foundUserType.isPresent());
        assertEquals(savedUserType.getUserTypeId(), foundUserType.get().getUserTypeId());
        assertEquals(savedUserType.getUserTypeName(), foundUserType.get().getUserTypeName());
    }

    @Test
    @DisplayName("Should not find user type with non-existent ID")
    void shouldNotFindUserTypeWithNonExistentId() {
        // Act
        Optional<UserType> foundUserType = userTypeRepository.findById(999);

        // Assert
        assertTrue(foundUserType.isEmpty());
    }

    @Test
    @DisplayName("Should find all user types")
    void shouldFindAllUserTypes() {
        // Arrange
        int initialCount = userTypeRepository.findAll().size();
        
        UserType newUserType = UserType.builder()
                .userTypeName("TEST_TYPE_" + System.currentTimeMillis())
                .build();
        userTypeRepository.save(newUserType);

        // Act
        List<UserType> userTypes = userTypeRepository.findAll();

        // Assert
        assertEquals(initialCount + 1, userTypes.size());
        assertTrue(userTypes.stream()
                .map(UserType::getUserTypeName)
                .anyMatch(name -> name.equals(newUserType.getUserTypeName())));
    }

    @Test
    @DisplayName("Should update user type successfully")
    void shouldUpdateUserTypeSuccessfully() {
        // Arrange
        UserType savedUserType = userTypeRepository.save(testUserType);
        String updatedTypeName = "UPDATED_TYPE";
        savedUserType.setUserTypeName(updatedTypeName);

        // Act
        UserType updatedUserType = userTypeRepository.save(savedUserType);

        // Assert
        assertEquals(updatedTypeName, updatedUserType.getUserTypeName());
        assertEquals(savedUserType.getUserTypeId(), updatedUserType.getUserTypeId());
    }

    @Test
    @DisplayName("Should delete user type successfully")
    void shouldDeleteUserTypeSuccessfully() {
        // Arrange
        UserType savedUserType = userTypeRepository.save(testUserType);

        // Act
        userTypeRepository.delete(savedUserType);
        Optional<UserType> deletedUserType = userTypeRepository.findById(savedUserType.getUserTypeId());

        // Assert
        assertTrue(deletedUserType.isEmpty());
    }

    @Test
    @DisplayName("Should save multiple user types")
    void shouldSaveMultipleUserTypes() {
        // Arrange
        int initialCount = userTypeRepository.findAll().size();
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        UserType type1 = UserType.builder()
                .userTypeName("TEST_TYPE_1_" + timestamp)
                .build();

        UserType type2 = UserType.builder()
                .userTypeName("TEST_TYPE_2_" + timestamp)
                .build();

        UserType type3 = UserType.builder()
                .userTypeName("TEST_TYPE_3_" + timestamp)
                .build();

        // Act
        List<UserType> newTypes = userTypeRepository.saveAll(List.of(type1, type2, type3));
        List<UserType> allTypes = userTypeRepository.findAll();

        // Assert
        assertEquals(3, newTypes.size());
        assertEquals(initialCount + 3, allTypes.size());
        assertTrue(allTypes.stream()
                .map(UserType::getUserTypeName)
                .anyMatch(name -> name.equals(type1.getUserTypeName())));
        assertTrue(allTypes.stream()
                .map(UserType::getUserTypeName)
                .anyMatch(name -> name.equals(type2.getUserTypeName())));
        assertTrue(allTypes.stream()
                .map(UserType::getUserTypeName)
                .anyMatch(name -> name.equals(type3.getUserTypeName())));
    }
} 