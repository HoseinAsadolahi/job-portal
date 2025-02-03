package se.jobportal.service;
import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.User;
import se.jobportal.repository.JobSeekerProfileRepository;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.JobSeekerProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





public class JobSeekerProfileServiceTest {

    @Mock
    private JobSeekerProfileRepository jpr;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JobSeekerProfileService jobSeekerProfileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOne() {
        JobSeekerProfile profile = new JobSeekerProfile();
        when(jpr.findById(1)).thenReturn(Optional.of(profile));

        Optional<JobSeekerProfile> result = jobSeekerProfileService.getOne(1);
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }

    @Test
    public void testAddNew() {
        JobSeekerProfile profile = new JobSeekerProfile();
        when(jpr.save(profile)).thenReturn(profile);

        JobSeekerProfile result = jobSeekerProfileService.addNew(profile);
        assertEquals(profile, result);
    }

    @Test
    public void testGetCurrentUserProfile_UserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobSeekerProfileService.getCurrentUserProfile();
        });

        assertEquals("User not found", exception.getMessage());
        }

        @Test
        public void testGetCurrentUserProfile_AnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        SecurityContextHolder.setContext(securityContext);

        JobSeekerProfile result = jobSeekerProfileService.getCurrentUserProfile();
        assertNull(result);
        }

        @Test
        public void testGetCurrentUserProfile_Success() {
        User user = new User();
        user.setUserId(1);
        JobSeekerProfile profile = new JobSeekerProfile();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jpr.findById(1)).thenReturn(Optional.of(profile));
        SecurityContextHolder.setContext(securityContext);

        JobSeekerProfile result = jobSeekerProfileService.getCurrentUserProfile();
        assertEquals(profile, result);
    }
}