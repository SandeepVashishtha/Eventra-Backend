package com.eventra;

import com.eventra.dto.LoginRequest;
import com.eventra.dto.SignupRequest;
import com.eventra.exception.GlobalExceptionHandler;
import com.eventra.exception.InvalidCredentialsException;
import com.eventra.exception.UserAlreadyExistsException;
import com.eventra.exception.UserNotFoundException;
import com.eventra.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ErrorHandlingTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Email already exists!");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
        
        ResponseEntity<?> response = exceptionHandler.handleUserAlreadyExistsException(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
        
        ResponseEntity<?> response = exceptionHandler.handleUserNotFoundException(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
        
        ResponseEntity<?> response = exceptionHandler.handleInvalidCredentialsException(ex, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
        
        ResponseEntity<?> response = exceptionHandler.handleBadCredentialsException(ex, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testValidationUtil() {
        // Test email validation
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));

        // Test password validation
        assertTrue(ValidationUtil.isValidPassword("Password123!"));
        assertFalse(ValidationUtil.isValidPassword("weak"));
        assertFalse(ValidationUtil.isValidPassword(""));
        assertFalse(ValidationUtil.isValidPassword(null));

        // Test name validation
        assertTrue(ValidationUtil.isValidName("John Doe"));
        assertFalse(ValidationUtil.isValidName("John123"));
        assertFalse(ValidationUtil.isValidName(""));
        assertFalse(ValidationUtil.isValidName(null));

        // Test input sanitization
        assertEquals("clean text", ValidationUtil.sanitizeInput("clean text"));
        assertEquals("clean text", ValidationUtil.sanitizeInput("  clean   text  "));
        assertEquals("cleanscripttext", ValidationUtil.sanitizeInput("clean<script>text"));
        assertNull(ValidationUtil.sanitizeInput(null));

        // Test blank validation
        assertTrue(ValidationUtil.isNotBlank("valid"));
        assertFalse(ValidationUtil.isNotBlank(""));
        assertFalse(ValidationUtil.isNotBlank("   "));
        assertFalse(ValidationUtil.isNotBlank(null));
    }

    @Test
    public void testSignupRequestValidation() {
        SignupRequest request = new SignupRequest();
        
        // Test valid data
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");
        
        assertTrue(ValidationUtil.isValidName(request.getFirstName()));
        assertTrue(ValidationUtil.isValidName(request.getLastName()));
        assertTrue(ValidationUtil.isValidEmail(request.getEmail()));
        assertTrue(ValidationUtil.isValidPassword(request.getPassword()));
    }

    @Test
    public void testLoginRequestValidation() {
        LoginRequest request = new LoginRequest();
        
        // Test valid data
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");
        
        assertTrue(ValidationUtil.isValidEmail(request.getEmail()));
        assertTrue(ValidationUtil.isNotBlank(request.getPassword()));
    }
} 