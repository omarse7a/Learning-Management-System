package com.dev.LMS;

import com.dev.LMS.controller.UserController;
import com.dev.LMS.dto.RegisterDto;
import com.dev.LMS.dto.UpdateProfileDto;
import com.dev.LMS.model.User;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private UserController userController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetUserProfile() {
        User user = mock(User.class);
        user.setEmail("test@example.com");

        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<User> response = userController.getUserProfile();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testUpdateUserProfile() {
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        User updatedUser = mock(User.class);
        updatedUser.setEmail("test@example.com");

        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.updateUser(any(String.class), any(UpdateProfileDto.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUserProfile(updateProfileDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    public void testGetAllUsers() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUserById() {
        User user = mock(User.class);
        user.setId(1);

        when(userService.getUserById(1)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testCreateUser() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setRole("ROLE_USER");
        registerDto.setName("Test User");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");

        User user = mock(User.class);
        user.setEmail("test@example.com");

        when(userFactory.createUser(any(String.class), any(String.class), any(String.class))).thenReturn(user);
        doNothing().when(userService).register(any(User.class));

        ResponseEntity<Map<String, String>> response = userController.createUser(registerDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody().get("message"));
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(204, response.getStatusCode().value());
    }
}
