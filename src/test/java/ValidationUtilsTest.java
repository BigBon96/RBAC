import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void isValidUsername_acceptsCorrectUsernames() {
        assertTrue(ValidationUtils.isValidUsername("john_doe"));
        assertTrue(ValidationUtils.isValidUsername("user123"));
        assertTrue(ValidationUtils.isValidUsername("abc"));
    }

    @Test
    void isValidUsername_rejectsInvalidUsernames() {
        assertFalse(ValidationUtils.isValidUsername(null));
        assertFalse(ValidationUtils.isValidUsername("ab"));          // слишком короткий
        assertFalse(ValidationUtils.isValidUsername("a".repeat(21))); // слишком длинный
        assertFalse(ValidationUtils.isValidUsername("john doe"));    // пробел
        assertFalse(ValidationUtils.isValidUsername("john-doe"));    // недопустимый символ
    }

    @Test
    void isValidEmail_acceptsCorrectEmails() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name+tag@sub.domain.org"));
    }

    @Test
    void isValidEmail_rejectsInvalidEmails() {
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("missing-at.com"));
        assertFalse(ValidationUtils.isValidEmail("user@domain"));
    }

    @Test
    void isValidDate_checksBasicFormat() {
        assertTrue(ValidationUtils.isValidDate("2024-01-01"));
        assertFalse(ValidationUtils.isValidDate(null));
        assertFalse(ValidationUtils.isValidDate("2024-1-1"));
        assertFalse(ValidationUtils.isValidDate("01-01-2024"));
        assertFalse(ValidationUtils.isValidDate("2024/01/01"));
    }

    @Test
    void normalizeString_trimsAndCollapsesSpaces() {
        assertEquals("hello world", ValidationUtils.normalizeString("  hello   world  "));
        assertEquals("", ValidationUtils.normalizeString("   "));
        assertEquals("", ValidationUtils.normalizeString(null));
    }

    @Test
    void requireNonEmpty_throwsOnEmptyOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonEmpty("", "field"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonEmpty("   ", "field"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonEmpty("   ", null));
    }

    @Test
    void requireNonEmpty_allowsNonBlank() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty("value", "field"));
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty(" value ", "field"));
    }
}

