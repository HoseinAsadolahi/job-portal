package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.entity.UserType;
import com.hosein.jobportal.exception.DuplicateEmailException;
import com.hosein.jobportal.services.UserService;
import com.hosein.jobportal.services.UserTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserTypeService userTypeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should display register page with user types")
    void shouldDisplayRegisterPage() {
        // Arrange
        List<UserType> userTypes = Arrays.asList(
            new UserType(1, "Job Seeker", null),
            new UserType(2, "Recruiter", null)
        );
        when(userTypeService.findAll()).thenReturn(userTypes);

        // Act
        String viewName = userController.register(model);

        // Assert
        assertEquals("register", viewName);
        verify(model).addAttribute("getAllTypes", userTypes);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.register(testUser, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
        verify(userService).save(testUser);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    @DisplayName("Should handle validation errors during registration")
    void shouldHandleValidationErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.register(testUser, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/register", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("validationErrors"), any());
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should handle duplicate email during registration")
    void shouldHandleDuplicateEmail() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateEmailException("Email already exists!"))
            .when(userService).save(any(User.class));

        // Act
        String viewName = userController.register(testUser, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/register", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Email already exists!");
    }

    @Test
    @DisplayName("Should display login page")
    void shouldDisplayLoginPage() {
        // Act
        String viewName = userController.login();

        // Assert
        assertEquals("login", viewName);
    }

    @Test
    @DisplayName("Should handle logout successfully")
    void shouldHandleLogoutSuccessfully() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        String viewName = userController.logout(request, response);

        // Assert
        assertEquals("redirect:/", viewName);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}