package com.rhcloud.cervex_jsoftware95.validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest {

    private EmailValidator validator;

    @Before
    public void setUp() {
        validator = new EmailValidator();
    }

    @Test
    public void testNullValue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    public void testEmptyString() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    public void testValidEmail() {
        String validEmail = "user@example.com";
        assertTrue(validator.isValid(validEmail, null));
    }

    @Test
    public void testInvalidEmailWithoutLocalPart() {
        String invalidEmail = "@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithoutDomain() {
        String invalidEmail = "user@";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithInvalidLocalPart() {
        String invalidEmail = "user name@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithInvalidDomain() {
        String invalidEmail = "user@example.com.";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithMultipleDotsInLocalPart() {
        String invalidEmail = "user.name@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithLeadingDotInLocalPart() {
        String invalidEmail = ".user@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithTrailingDotInLocalPart() {
        String invalidEmail = "user.@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithLeadingDotInDomain() {
        String invalidEmail = "user@.example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithTrailingDotInDomain() {
        String invalidEmail = "user@example.com.";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithSpecialCharactersInLocalPart() {
        String invalidEmail = "user!@#$%^&*()@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }

    @Test
    public void testInvalidEmailWithSpacesInLocalPart() {
        String invalidEmail = "user name@example.com";
        assertFalse(validator.isValid(invalidEmail, null));
    }
}

