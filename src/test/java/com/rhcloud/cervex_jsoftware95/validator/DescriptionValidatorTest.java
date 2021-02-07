package com.rhcloud.cervex_jsoftware95.validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DescriptionValidatorTest {

    private DescriptionValidator validator;

    @Before
    public void setUp() {
        validator = new DescriptionValidator();
    }

    @Test
    public void testNullValue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    public void testEmptyValue() {
        assertTrue(validator.isValid("", null));
    }

    @Test
    public void testValidDescription() {
        String validDescription = "This is a valid description";
        assertTrue(validator.isValid(validDescription, null));
    }

    @Test
    public void testValidDescriptionWithExactly15Characters() {
        String validDescription = "This is valid.";
        assertTrue(validator.isValid(validDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithMoreThan15Characters() {
        String invalidDescription = "This is a very long description that has more than 15 characters";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithRepeatedCharactersAtStart() {
        String invalidDescription = "AAThis is a valid description";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithRepeatedCharactersInMiddle() {
        String invalidDescription = "This is a descrrription";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithRepeatedCharactersAtEnd() {
        String invalidDescription = "This is a valid descriptionss";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithMultipleRepeatedCharacters() {
        String invalidDescription = "This has aaa bbb ccc description";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithDifferentRepeatedCharacters() {
        String invalidDescription = "This has aab aba abb description";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithRepeatedUnicodeCharacters() {
        String invalidDescription = "This description has ";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithRepeatedWhitespace() {
        String invalidDescription = "This  description  has  too  much  whitespace";
        assertFalse(validator.isValid(invalidDescription, null));
    }

    @Test
    public void testInvalidDescriptionWithSpecialCharacters() {
        String invalidDescription = "This description has special characters like *!@#$%^&*()";
        assertFalse(validator.isValid(invalidDescription, null));
    }
}
