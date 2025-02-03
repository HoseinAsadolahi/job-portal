package se.jobportal.service;

import se.jobportal.entity.UserType;
import se.jobportal.repository.UserTypeRepository;
import se.jobportal.services.UserTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;




public class UserTypeServiceTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeService userTypeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        UserType userType1 = new UserType();
        userType1.setUserTypeId(1);
        userType1.setUserTypeName("Admin");

        UserType userType2 = new UserType();
        userType2.setUserTypeId(2);
        userType2.setUserTypeName("User");

        List<UserType> userTypes = Arrays.asList(userType1, userType2);

        when(userTypeRepository.findAll()).thenReturn(userTypes);

        List<UserType> result = userTypeService.findAll();

        assertEquals(2, result.size());
        assertEquals("Admin", result.get(0).getUserTypeName());
        assertEquals("User", result.get(1).getUserTypeName());
    }
}
