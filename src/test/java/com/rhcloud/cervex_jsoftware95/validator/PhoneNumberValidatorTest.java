package com.rhcloud.cervex_jsoftware95.validator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidatorTest {

    private final PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    public void testNullValue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    public void testEmptyValue() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    public void testInvalidValueWithoutPlus() {
        assertFalse(validator.isValid("123456", null));
    }

    @Test
    public void testInvalidValueWithPlusLessThan7Digits() {
        assertFalse(validator.isValid("+123456", null));
    }

    @Test
    public void testInvalidValueWithPlusMoreThan20Digits() {
        assertFalse(validator.isValid("+123456789012345678901", null));
    }

    @Test
    public void testInvalidValueWithSpecialCharacters() {
        assertFalse(validator.isValid("+1234567890@", null));
    }

    @Test
    public void testValidValueWithoutPlus() {
        assertTrue(validator.isValid("1234567890", null));
    }

    @Test
    public void testValidValueWithPlus() {
        assertTrue(validator.isValid("+1234567890", null));
    }

    @Test
    public void testValidValueWithSpaces() {
        assertTrue(validator.isValid("123 456 7890", null));
    }

    @Test
    public void testValidValueWithInternationalPrefix() {
        assertTrue(validator.isValid("+213 555 123456", null));
    }

    @Test
    public void testInvalidValueWithLeadingCharacters() {
        assertFalse(validator.isValid("abc+1234567890", null));
    }

    @Test
    public void testInvalidValueWithTrailingCharacters() {
        assertFalse(validator.isValid("+1234567890def", null));
    }

    @Test
    public void testInvalidValueWithMultiplePlusSigns() {
        assertFalse(validator.isValid("++1234567890", null));
    }

    @Test
    public void testInvalidValueWithMixedLettersAndNumbers() {
        assertFalse(validator.isValid("+123a456b7890", null));
    }
}
