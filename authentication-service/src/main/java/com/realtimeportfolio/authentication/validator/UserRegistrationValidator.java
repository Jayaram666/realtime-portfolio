package com.realtimeportfolio.authentication.validator;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserRegistrationValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final Predicate<String> isNotBlank =
            value -> value != null && !value.trim().isEmpty();

    private final Predicate<String> isValidEmail =
            email -> email != null && EMAIL_PATTERN.matcher(email).matches();

    private final Predicate<String> hasMinimumEightCharacters =
            password -> password != null && password.length() >= 8;

    private final Predicate<String> hasUppercase =
            password -> password != null && password.chars().anyMatch(Character::isUpperCase);

    private final Predicate<String> hasLowercase =
            password -> password != null && password.chars().anyMatch(Character::isLowerCase);

    private final Predicate<String> hasDigit =
            password -> password != null && password.chars().anyMatch(Character::isDigit);

    private final Predicate<String> hasAllowedSpecialCharacter =
            password -> password != null && password.matches(".*[@#$%^*_-].*");

    public List<String> validate(UserRegistrationRequest request) {

        log.debug("Validating registration request. email={}", request != null ? request.getEmail() : null);
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("Request body cannot be empty");
            return errors;
        }

        if (!isNotBlank.test(request.getName())) {
            errors.add("Name is required");
        }

        if (!isNotBlank.test(request.getEmail())) {
            errors.add("Email is required");
        } else if (!isValidEmail.test(request.getEmail())) {
            errors.add("Email format is not correct");
        }

        if (!isNotBlank.test(request.getPassword())) {
            errors.add("Password is required");
        } else {
            validatePassword(request.getPassword(), errors);
        }

        if (errors.isEmpty()) {
            log.debug("Registration request validation passed. email={}", request.getEmail());
        } else {
            log.warn("Registration request validation failed. email={}, errorCount={}", request.getEmail(), errors.size());
        }
        return errors;
    }

    private void validatePassword(String password, List<String> errors) {

        if (!hasMinimumEightCharacters.test(password)) {
            errors.add("Password must be minimum 8 characters");
        }

        if (!hasUppercase.test(password)) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!hasLowercase.test(password)) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!hasDigit.test(password)) {
            errors.add("Password must contain at least one number");
        }

        if (!hasAllowedSpecialCharacter.test(password)) {
            errors.add("Password must contain at least one special character from @ # $ % ^ * _ -");
        }
    }
}