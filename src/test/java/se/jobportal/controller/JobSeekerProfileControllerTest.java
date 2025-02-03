package se.jobportal.controller;

import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.Skill;
import se.jobportal.entity.User;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.JobSeekerProfileService;
import se.jobportal.util.FileDownloadUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerProfileControllerTest {

    @Mock
    private JobSeekerProfileService jobSeekerProfileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private JobSeekerProfileController controller;

    private User testUser;
    private JobSeekerProfile testProfile;
    private List<Skill> testSkills;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .email("test@example.com")
                .build();

        testSkills = Arrays.asList(
            Skill.builder()
                .name("Java")
                .experienceLevel("Expert")
                .yearsOfExperience("5")
                .build()
        );

        testProfile = JobSeekerProfile.builder()
                .userAccountId(1)
                .firstName("John")
                .lastName("Doe")
                .city("Test City")
                .state("Test State")
                .country("Test Country")
                .user(testUser)
                .skills(testSkills)
                .build();
    }

    @Test
    @DisplayName("Should display job seeker profile for authenticated user with existing profile")
    void shouldDisplayProfileForAuthenticatedUserWithExistingProfile() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.of(testProfile));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.jobSeekerProfile(model);

        // Assert
        assertEquals("job-seeker-profile", viewName);
        verify(model).addAttribute("profile", testProfile);
        verify(model).addAttribute("skills", new ArrayList<>());
    }

    @Test
    @DisplayName("Should handle new profile creation for authenticated user")
    void shouldHandleNewProfileCreation() {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        MockMultipartFile pdfFile = new MockMultipartFile(
            "pdf",
            "test.pdf",
            "application/pdf",
            "test pdf content".getBytes()
        );

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jobSeekerProfileService.addNew(any(JobSeekerProfile.class))).thenReturn(testProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = controller.addNew(testProfile, imageFile, pdfFile, model);

        // Assert
        assertEquals("redirect:/dashboard/", viewName);
        verify(jobSeekerProfileService).addNew(testProfile);
        assertNotNull(testProfile.getProfilePhoto());
        assertNotNull(testProfile.getResume());
    }

    @Test
    @DisplayName("Should handle anonymous user access")
    void shouldHandleAnonymousUser() {
        // Arrange
        Authentication anonymousAuth = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        // Act
        String viewName = controller.jobSeekerProfile(model);

        // Assert
        assertEquals("job-seeker-profile", viewName);
        verify(userRepository, never()).findByEmail(anyString());
        verify(jobSeekerProfileService, never()).getOne(anyInt());
    }

    @Test
    @DisplayName("Should display candidate profile by ID")
    void shouldDisplayCandidateProfileById() {
        // Arrange
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.of(testProfile));

        // Act
        String viewName = controller.candidateProfile(1, model);

        // Assert
        assertEquals("job-seeker-profile", viewName);
        verify(model).addAttribute("profile", testProfile);
    }

    @Test
    @DisplayName("Should throw exception when profile not found by ID")
    void shouldThrowExceptionWhenProfileNotFound() {
        // Arrange
        when(jobSeekerProfileService.getOne(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            controller.candidateProfile(1, model)
        );
        }

        @Test
        @DisplayName("Should download resume successfully")
        void shouldDownloadResumeSuccessfully() throws IOException {
        // Arrange
        String fileName = "resume.pdf";
        String userId = "1";
        UrlResource resource = new UrlResource(Paths.get("test.pdf").toUri());
        MockedStatic<FileDownloadUtil> f = mockStatic(FileDownloadUtil.class);
        f.when(() -> FileDownloadUtil.getFileAsResource(anyString(), anyString())).thenReturn(resource);

        // Act
        ResponseEntity<?> response = controller.downloadResume(fileName, userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        }

        @AfterEach
        void tearDown() {
        SecurityContextHolder.clearContext();
    }
}