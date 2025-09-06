package com.eventra.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s]{2,50}$"
    );
    
    /**
     * Validates email format
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates password strength
     * @param password password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validates name format (letters and spaces only, 2-50 characters)
     * @param name name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    /**
     * Sanitizes input string by removing potentially dangerous characters
     * @param input input string to sanitize
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                   .replaceAll("[<>\"']", "")
                   .replaceAll("\\s+", " ");
    }
    
    /**
     * Validates that a string is not null, empty, or only whitespace
     * @param input string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }
} 