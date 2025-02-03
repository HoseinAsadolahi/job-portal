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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import se.jobportal.entity.*;
import se.jobportal.services.JobPostActivityService;
import se.jobportal.services.JobSeekerApplyService;
import se.jobportal.services.JobSeekerSaveService;
import se.jobportal.services.UserService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostActivityControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobPostActivityService jobPostActivityService;

    @Mock
    private JobSeekerApplyService jobSeekerApplyService;

    @Mock
    private JobSeekerSaveService jobSeekerSaveService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private JobPostActivityController controller;

    private JobPostActivity testJob;
    private RecruiterProfile testRecruiterProfile;
    private JobSeekerProfile testJobSeekerProfile;

    @BeforeEach
    void setUp() {
        testJob = JobPostActivity.builder()
            .JobPostId(1)
            .jobTitle("Software Engineer")
            .jobType("Full-time")
            .remote("Remote-Only")
            .salary("100K")
            .postedDate(new Date())
            .build();

        testRecruiterProfile = RecruiterProfile.builder()
            .userAccountId(1)
            .company("Test Company")
            .build();

        testJobSeekerProfile = JobSeekerProfile.builder()
            .userAccountId(1)
            .firstName("John")
            .lastName("Doe")
            .build();
    }

    @Test
    @DisplayName("Should show dashboard for recruiter")
    void shouldShowDashboardForRecruiter() {
        // Arrange
        Collection<SimpleGrantedAuthority> authorities = 
        Collections.singleton(new SimpleGrantedAuthority("Recruiter"));
        when(authentication.getAuthorities()).thenAnswer(advocation -> authorities);            
        when(userService.getCurrentUserProfile()).thenReturn(testRecruiterProfile);
        when(jobPostActivityService.getRecruiterJobs(anyInt())).thenReturn(Arrays.asList(new RecruiterJobsDto()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.searchJobs(model, null, null, null, 
            null, null, null, null, null, null, 
            false, false, false);

        // Assert
        assertEquals("dashboard", viewName);
        verify(model).addAttribute(eq("jobPost"), any());
    }

    @Test
    @DisplayName("Should show dashboard for job seeker")
    void shouldShowDashboardForJobSeeker() {
        // Arrange
        Collection<SimpleGrantedAuthority> authorities = 
            Collections.singleton(new SimpleGrantedAuthority("Job Seeker"));
        when(authentication.getAuthorities()).thenAnswer(advocation -> authorities);
        when(userService.getCurrentUserProfile()).thenReturn(testJobSeekerProfile);
        when(jobPostActivityService.getAll()).thenReturn(Arrays.asList(testJob));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.searchJobs(model, null, null, null, 
            null, null, null, null, null, null, 
            false, false, false);

        // Assert
        assertEquals("dashboard", viewName);
        verify(model).addAttribute(eq("jobPost"), any());
    }

    @Test
    @DisplayName("Should add new job")
    void shouldAddNewJob() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(new User());
        when(jobPostActivityService.addNew(any())).thenReturn(testJob);

        // Act
        String viewName = controller.addNewJob(testJob, model);

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobPostActivityService).addNew(testJob);
    }

    @Test
    @DisplayName("Should edit job")
    void shouldEditJob() {
        // Arrange
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);

        // Act
        String viewName = controller.edit(1, model);

        // Assert
        assertEquals("add-jobs", viewName);
        verify(model).addAttribute("jobPostActivity", testJob);
    }

    @Test
    @DisplayName("Should delete job")
    void shouldDeleteJob() {
        // Arrange
        when(jobPostActivityService.getOne(anyInt())).thenReturn(testJob);

        // Act
        String viewName = controller.delete(1);

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobPostActivityService).deleteOne(1);
        verify(jobSeekerApplyService).deleteAll(testJob);
        verify(jobSeekerSaveService).deleteAll(testJob);
    }

    @Test
    @DisplayName("Should perform global search with filters")
    void shouldPerformGlobalSearchWithFilters() {
        // Arrange
        when(jobPostActivityService.search(anyString(), anyString(), any(), any(), any()))
            .thenReturn(Arrays.asList(testJob));

        // Act
        String viewName = controller.globalSearch(model, "developer", "New York", 
            "Part-time", "Full-time", null, null, "Remote-Only", 
            null, null, true, false, false);

        // Assert
        assertEquals("global-search", viewName);
        verify(jobPostActivityService).search(anyString(), anyString(), any(), any(), any());
        verify(model).addAttribute(eq("jobPost"), any());
    }

    @Test
    @DisplayName("Should perform global search without filters")
    void shouldPerformGlobalSearchWithoutFilters() {
        // Arrange
        when(jobPostActivityService.getAll()).thenReturn(Arrays.asList(testJob));

        // Act
        String viewName = controller.globalSearch(model, null, null, 
            null, null, null, null, null, 
            null, null, false, false, false);

        // Assert
        assertEquals("global-search", viewName);
        verify(jobPostActivityService).getAll();
        verify(model).addAttribute(eq("jobPost"), any());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}