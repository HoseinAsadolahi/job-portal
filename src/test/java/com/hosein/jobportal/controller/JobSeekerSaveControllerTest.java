package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.*;
import com.hosein.jobportal.services.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerSaveControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobSeekerProfileService jobSeekerProfileService;

    @Mock
    private JobPostActivityService jobPostActivityService;

    @Mock
    private JobSeekerSaveService jobSeekerSaveService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private JobSeekerSaveController controller;

    private User testUser;
    private JobSeekerProfile testProfile;
    private JobPostActivity testJob;
    private JobSeekerSave testSave;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .email("test@example.com")
                .build();

        testProfile = JobSeekerProfile.builder()
                .userAccountId(1)
                .firstName("John")
                .lastName("Doe")
                .user(testUser)
                .build();

        testJob = JobPostActivity.builder()
                .JobPostId(1)
                .jobTitle("Software Engineer")
                .postedDate(new Date())
                .build();

        testSave = JobSeekerSave.builder()
                .id(1)
                .userId(testProfile)
                .job(testJob)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should save job successfully")
    void shouldSaveJobSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.of(testProfile));
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.save(1, new JobSeekerSave());

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobSeekerSaveService).addNew(any(JobSeekerSave.class));
    }

    @Test
    @DisplayName("Should handle anonymous user for save")
    void shouldHandleAnonymousUserForSave() {
        // Arrange
        Authentication anonymousAuth = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        // Act
        String viewName = controller.save(1, new JobSeekerSave());

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobSeekerSaveService, never()).addNew(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found for save")
    void shouldThrowExceptionWhenUserNotFoundForSave() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            controller.save(1, new JobSeekerSave())
        );
    }

    @Test
    @DisplayName("Should display saved jobs")
    void shouldDisplaySavedJobs() {
        // Arrange
        when(userService.getCurrentUserProfile()).thenReturn(testProfile);
        when(jobSeekerSaveService.getCandidatesJobs(any(JobSeekerProfile.class)))
            .thenReturn(Arrays.asList(testSave));

        // Act
        String viewName = controller.savedJobs(model);

        // Assert
        assertEquals("saved-jobs", viewName);
        verify(model).addAttribute("jobPost", Arrays.asList(testJob));
        verify(model).addAttribute("user", testProfile);
    }

    @Test
    @DisplayName("Should handle no saved jobs")
    void shouldHandleNoSavedJobs() {
        // Arrange
        when(userService.getCurrentUserProfile()).thenReturn(testProfile);
        when(jobSeekerSaveService.getCandidatesJobs(any(JobSeekerProfile.class)))
            .thenReturn(Arrays.asList());

        // Act
        String viewName = controller.savedJobs(model);

        // Assert
        assertEquals("saved-jobs", viewName);
        verify(model).addAttribute("jobPost", Arrays.asList());
    }
}