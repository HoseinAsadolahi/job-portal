package se.jobportal.service;

import se.jobportal.entity.RecruiterProfile;
import se.jobportal.entity.User;
import se.jobportal.repository.RecruiterProfileRepository;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.RecruiterProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





public class RecruiterProfileServiceTest {

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RecruiterProfileService recruiterProfileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        int recruiterId = 1;
        RecruiterProfile recruiterProfile = new RecruiterProfile();
        when(recruiterProfileRepository.findById(recruiterId)).thenReturn(Optional.of(recruiterProfile));

        Optional<RecruiterProfile> result = recruiterProfileService.findById(recruiterId);

        assertTrue(result.isPresent());
        assertEquals(recruiterProfile, result.get());
        verify(recruiterProfileRepository, times(1)).findById(recruiterId);
    }

    @Test
    public void testFindByIdNotFound() {
        int recruiterId = 1;
        when(recruiterProfileRepository.findById(recruiterId)).thenReturn(Optional.empty());

        Optional<RecruiterProfile> result = recruiterProfileService.findById(recruiterId);

        assertFalse(result.isPresent());
        verify(recruiterProfileRepository, times(1)).findById(recruiterId);
    }

    @Test
    public void testAddNew() {
        RecruiterProfile recruiterProfile = new RecruiterProfile();
        when(recruiterProfileRepository.save(recruiterProfile)).thenReturn(recruiterProfile);

        RecruiterProfile result = recruiterProfileService.addNew(recruiterProfile);

        assertEquals(recruiterProfile, result);
        verify(recruiterProfileRepository, times(1)).save(recruiterProfile);
    }

    @Test
    public void testGetCurrentRecruiterProfile() {
        String username = "test@example.com";
        User user = new User();
        user.setUserId(1);
        RecruiterProfile recruiterProfile = new RecruiterProfile();
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(recruiterProfileRepository.findById(user.getUserId())).thenReturn(Optional.of(recruiterProfile));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RecruiterProfile result = recruiterProfileService.getCurrentRecruiterProfile();

        assertNotNull(result);
        assertEquals(recruiterProfile, result);
        verify(userRepository, times(1)).findByEmail(username);
        verify(recruiterProfileRepository, times(1)).findById(user.getUserId());
    }

    @Test
    public void testGetCurrentRecruiterProfileUserNotFound() {
        String username = "test@example.com";
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recruiterProfileService.getCurrentRecruiterProfile();
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(username);
        verify(recruiterProfileRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testGetCurrentRecruiterProfileAnonymousUser() {
        SecurityContextHolder.getContext().setAuthentication(mock(AnonymousAuthenticationToken.class));

        RecruiterProfile result = recruiterProfileService.getCurrentRecruiterProfile();

        assertNull(result);
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(recruiterProfileRepository, times(0)).findById(anyInt());
    }
}