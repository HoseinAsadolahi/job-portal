package se.jobportal.controller;

import se.jobportal.entity.*;
import se.jobportal.services.*;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import se.jobportal.entity.*;
import se.jobportal.services.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerApplyControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobPostActivityService jobPostActivityService;

    @Mock
    private JobSeekerApplyService jobSeekerApplyService;

    @Mock
    private JobSeekerSaveService jobSeekerSaveService;

    @Mock
    private RecruiterProfileService recruiterProfileService;

    @Mock
    private JobSeekerProfileService jobSeekerProfileService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private JobSeekerApplyController controller;

    private User testUser;
    private JobPostActivity testJob;
    private JobSeekerProfile testJobSeekerProfile;
    private RecruiterProfile testRecruiterProfile;
    private JobSeekerApply testApplication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .email("test@example.com")
                .build();

        testJob = JobPostActivity.builder()
                .JobPostId(1)
                .jobTitle("Software Engineer")
                .build();

        testJobSeekerProfile = JobSeekerProfile.builder()
                .userAccountId(1)
                .firstName("John")
                .lastName("Doe")
                .user(testUser)
                .build();

        testRecruiterProfile = RecruiterProfile.builder()
                .userAccountId(1)
                .company("Test Company")
                .user(testUser)
                .build();

        testApplication = JobSeekerApply.builder()
                .id(1)
                .userId(testJobSeekerProfile)
                .job(testJob)
                .applyDate(new Date())
                .build();
    }

    @Test
    @DisplayName("Should display job details for recruiter")
    void shouldDisplayJobDetailsForRecruiter() {
        // Arrange
        Collection<SimpleGrantedAuthority> authorities = 
            Collections.singleton(new SimpleGrantedAuthority("Recruiter"));
        
        when(authentication.getAuthorities()).thenAnswer(advocation -> authorities);
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);
        when(recruiterProfileService.getCurrentRecruiterProfile()).thenReturn(testRecruiterProfile);
        when(jobSeekerApplyService.getJobCandidates(any())).thenReturn(Arrays.asList(testApplication));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.display(1, model);

        // Assert
        assertEquals("job-details", viewName);
        verify(model).addAttribute("applyList", Arrays.asList(testApplication));
        verify(model).addAttribute("jobDetails", testJob);
    }

    @Test
    @DisplayName("Should display job details for job seeker")
    void shouldDisplayJobDetailsForJobSeeker() {
        // Arrange
        Collection<SimpleGrantedAuthority> authorities = 
            Collections.singleton(new SimpleGrantedAuthority("Job Seeker"));
        
        when(authentication.getAuthorities()).thenAnswer(advocation -> authorities);
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);
        when(jobSeekerProfileService.getCurrentUserProfile()).thenReturn(testJobSeekerProfile);
        when(jobSeekerApplyService.getJobCandidates(any())).thenReturn(new ArrayList<>());
        when(jobSeekerSaveService.getJobCandidates(any())).thenReturn(new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.display(1, model);

        // Assert
        assertEquals("job-details", viewName);
        verify(model).addAttribute("alreadyApplied", false);
        verify(model).addAttribute("alreadySaved", false);
    }

    @Test
    @DisplayName("Should handle anonymous user for display")
    void shouldHandleAnonymousUserForDisplay() {
        // Arrange
        Authentication anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        // Act
        String viewName = controller.display(1, model);

        // Assert
        assertEquals("job-details", viewName);
        verify(recruiterProfileService, never()).getCurrentRecruiterProfile();
        verify(jobSeekerProfileService, never()).getCurrentUserProfile();
    }

    @Test
    @DisplayName("Should apply for job successfully")
    void shouldApplyForJobSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.of(testJobSeekerProfile));
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.apply(1, model);

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobSeekerApplyService).addNew(any(JobSeekerApply.class));
    }

    @Test
    @DisplayName("Should handle anonymous user for apply")
    void shouldHandleAnonymousUserForApply() {
        // Arrange
        Authentication anonymousAuth = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        // Act
        String viewName = controller.apply(1, model);

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobSeekerApplyService, never()).addNew(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found for apply")
    void shouldThrowExceptionWhenUserNotFoundForApply() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            controller.apply(1, model)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}