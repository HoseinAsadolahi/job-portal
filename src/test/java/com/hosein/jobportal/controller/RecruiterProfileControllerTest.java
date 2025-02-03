package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.RecruiterProfile;
import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.repository.UserRepository;
import com.hosein.jobportal.services.RecruiterProfileService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecruiterProfileService recruiterProfileService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RecruiterProfileController controller;

    private User testUser;
    private RecruiterProfile testProfile;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .email("test@example.com")
                .build();

        testProfile = RecruiterProfile.builder()
                .userAccountId(1)
                .firstName("John")
                .lastName("Doe")
                .company("Test Company")
                .user(testUser)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldDisplayProfileForAuthenticatedUserWithExistingProfile() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recruiterProfileService.findById(anyInt())).thenReturn(Optional.of(testProfile));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.editRecruiterProfile(model);

        // Assert
        assertEquals("recruiter_profile", viewName);
        verify(model).addAttribute("profile", testProfile);
        verify(userRepository).findByEmail("test@example.com");
        verify(recruiterProfileService).findById(testUser.getUserId());
    }

    @Test
    void shouldHandleAuthenticatedUserWithoutProfile() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recruiterProfileService.findById(anyInt())).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.editRecruiterProfile(model);

        // Assert
        assertEquals("recruiter_profile", viewName);
        verify(model, never()).addAttribute(eq("profile"), any());
    }

    @Test
    void shouldHandleAnonymousUser() {
        // Arrange
        Authentication anonymousAuth = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        // Act
        String viewName = controller.editRecruiterProfile(model);

        // Assert
        assertEquals("recruiter_profile", viewName);
        verify(userRepository, never()).findByEmail(anyString());
        verify(recruiterProfileService, never()).findById(anyInt());
    }

    @Test
    void shouldCreateNewProfileWithImage() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recruiterProfileService.addNew(any(RecruiterProfile.class))).thenReturn(testProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.addNew(testProfile, file, model);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
        verify(recruiterProfileService).addNew(testProfile);
        assertEquals(testUser.getUserId(), testProfile.getUserAccountId());
        assertEquals(testUser, testProfile.getUser());
    }

    @Test
    void shouldCreateNewProfileWithoutImage() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image",
            "",
            "image/jpeg",
            new byte[0]
        );

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recruiterProfileService.addNew(any(RecruiterProfile.class))).thenReturn(testProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.addNew(testProfile, emptyFile, model);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
        verify(recruiterProfileService).addNew(testProfile);
        assertNull(testProfile.getProfilePhoto());
    }

    @Test
    void shouldHandleUserNotFoundDuringProfileCreation() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            controller.addNew(testProfile, file, model)
        );
        verify(recruiterProfileService, never()).addNew(any());
    }
}