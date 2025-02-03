package com.hosein.jobportal.repository;

import com.hosein.jobportal.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        assertNotNull(savedUser);
        assertTrue(savedUser.getUserId() > 0);
        assertEquals(testUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should not find user with non-existent email")
    void shouldNotFindUserWithNonExistentEmail() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmailConstraint() {
        // Arrange
        userRepository.save(testUser);

        User duplicateUser = User.builder()
                .email("test@example.com")
                .password("differentpassword")
                .isActive(true)
                .registrationDate(new Date())
                .build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicateUser);
            userRepository.flush();
        });
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        savedUser.setPassword("newpassword123");
        savedUser.setActive(false);

        // Act
        User updatedUser = userRepository.save(savedUser);

        // Assert
        assertEquals("newpassword123", updatedUser.getPassword());
        assertFalse(updatedUser.isActive());
        assertEquals(savedUser.getUserId(), updatedUser.getUserId());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        // Act
        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(savedUser.getUserId());

        // Assert
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // Arrange
        userRepository.save(testUser);
        
        User secondUser = User.builder()
                .email("second@example.com")
                .password("password456")
                .isActive(true)
                .registrationDate(new Date())
                .build();
        userRepository.save(secondUser);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 2);
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUserId(), foundUser.get().getUserId());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }
} 