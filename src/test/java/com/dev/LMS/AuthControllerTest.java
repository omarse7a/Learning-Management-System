package com.dev.LMS;

import com.dev.LMS.controller.AuthController;
import com.dev.LMS.dto.RegisterDto;
import com.dev.LMS.dto.UserLoginDto;
import com.dev.LMS.model.Role;
import com.dev.LMS.model.Student;
import com.dev.LMS.model.User;
import com.dev.LMS.service.UserService;
import com.dev.LMS.util.UserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private UserFactory userFactory;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("John Doe");
        registerDto.setEmail("john.doe@example.com");
        registerDto.setPassword("password123");
        registerDto.setRole("STUDENT");

        User user = mock(Student.class);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(Role.STUDENT);

        when(userFactory.createUser(anyString(), anyString(), anyString())).thenReturn(user);
        doNothing().when(userService).register(any(User.class));

        ResponseEntity<Map<String, String>> response = authController.register(registerDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody().get("message"));
    }

    @Test
    public void testLogin_Success() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("john.doe@example.com");
        userLoginDto.setPassword("password123");
        userLoginDto.setRole("STUDENT");

        User user = mock(Student.class);
        when(user.getEmail()).thenReturn("john.doe@example.com");
        when(user.getPassword()).thenReturn("password123");
        when(user.getRole()).thenReturn(Role.STUDENT);

        when(userFactory.tempLoginUser(anyString(), anyString())).thenReturn(user);
        when(userService.login(any(User.class))).thenReturn("token");

        ResponseEntity<Map<String, String>> response = authController.login(userLoginDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals("token", response.getBody().get("token"));
    }

    @Test
    public void testLogin_MissingEmail() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(null);
        userLoginDto.setPassword("password123");
        userLoginDto.setRole("STUDENT");

        ResponseEntity<Map<String, String>> response = authController.login(userLoginDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid login credentials.", response.getBody().get("message"));
    }

    @Test
    public void testLogin_MissingPassword() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("john.doe@example.com");
        userLoginDto.setPassword(null);
        userLoginDto.setRole("STUDENT");

        ResponseEntity<Map<String, String>> response = authController.login(userLoginDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid login credentials.", response.getBody().get("message"));
    }

    @Test
    public void testLogin_WrongPassword() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setRole("STUDENT");
        userLoginDto.setEmail("john.doe@example.com");
        userLoginDto.setPassword("wrong123");

        User userMock = mock(Student.class);
        when(userMock.getEmail()).thenReturn("john.doe@example.com");
        // This simulates the real stored password
        when(userMock.getPassword()).thenReturn("password123");

        when(userFactory.tempLoginUser(anyString(), anyString())).thenReturn(userMock);
        when(userService.login(any(User.class)))
                .thenThrow(new RuntimeException("Invalid login credentials."));

        ResponseEntity<Map<String, String>> response = authController.login(userLoginDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error: Invalid login credentials.", response.getBody().get("message"));
    }
}
