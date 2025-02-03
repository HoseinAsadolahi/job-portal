package se.jobportal.service;

import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.RecruiterProfile;
import se.jobportal.entity.User;
import se.jobportal.entity.UserType;
import se.jobportal.exception.DuplicateEmailException;
import se.jobportal.repository.JobSeekerProfileRepository;
import se.jobportal.repository.RecruiterProfileRepository;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerProfileRepository jspRepository;

    @Mock
    private RecruiterProfileRepository rpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserType jobSeekerType;
    private UserType recruiterType;

    @BeforeEach
    void setUp() {
        jobSeekerType = UserType.builder()
                .userTypeId(2)
                .userTypeName("Job Seeker")
                .build();

        recruiterType = UserType.builder()
                .userTypeId(1)
                .userTypeName("Recruiter")
                .build();

        testUser = User.builder()
                .userId(1)
                .email("test@example.com")
                .password("password123")
                .isActive(true)
                .registrationDate(new Date())
                .userType(jobSeekerType)
                .build();
    }

    @Test
    @DisplayName("Should save job seeker successfully")
    void shouldSaveJobSeeker() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jspRepository.save(any(JobSeekerProfile.class))).thenReturn(new JobSeekerProfile(testUser));

        // Act
        User savedUser = userService.save(testUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getRegistrationDate());
        verify(jspRepository).save(any(JobSeekerProfile.class));
    }

    @Test
    @DisplayName("Should save recruiter successfully")
    void shouldSaveRecruiter() {
        // Arrange
        testUser.setUserType(recruiterType);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(rpRepository.save(any(RecruiterProfile.class))).thenReturn(new RecruiterProfile(testUser));

        // Act
        User savedUser = userService.save(testUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getRegistrationDate());
        verify(rpRepository).save(any(RecruiterProfile.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email exists")
    void shouldThrowDuplicateEmailException() {
        // Arrange
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.save(testUser));
    }

    @Test
    @DisplayName("Should get current user profile for job seeker")
    void shouldGetCurrentUserProfileForJobSeeker() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.getAuthorities()).thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("Job Seeker")));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jspRepository.findById(anyInt())).thenReturn(Optional.of(new JobSeekerProfile(testUser)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Object profile = userService.getCurrentUserProfile();

        // Assert
        assertNotNull(profile);
        assertTrue(profile instanceof JobSeekerProfile);
    }

    @Test
    @DisplayName("Should get current user profile for recruiter")
    void shouldGetCurrentUserProfileForRecruiter() {
        // Arrange
        testUser.setUserType(recruiterType);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.getAuthorities()).thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER")));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(rpRepository.findById(anyInt())).thenReturn(Optional.of(new RecruiterProfile(testUser)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Object profile = userService.getCurrentUserProfile();

        // Assert
        assertNotNull(profile);
        assertTrue(profile instanceof RecruiterProfile);
    }

    @Test
    @DisplayName("Should return null when getting profile for anonymous user")
    void shouldReturnNullForAnonymousUser() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        // Act
        Object profile = userService.getCurrentUserProfile();

        // Assert
        assertNull(profile);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmail() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findByEmail("test@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found by email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.findByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should get current authenticated user")
    void shouldGetCurrentAuthenticatedUser() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        User currentUser = userService.getCurrentUser();

        // Assert
        assertNotNull(currentUser);
        assertEquals("test@example.com", currentUser.getEmail());
    }

    @Test
    @DisplayName("Should return null for anonymous user when getting current user")
    void shouldReturnNullForAnonymousUserWhenGettingCurrentUser() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        // Act
        User currentUser = userService.getCurrentUser();

        // Assert
        assertNull(currentUser);
    }
}