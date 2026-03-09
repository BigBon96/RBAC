import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void addDays_addsCorrectNumberOfDays() {
        String result = DateUtils.addDays("2024-01-01", 10);
        assertEquals("2024-01-11", result);
    }

    @Test
    void isBeforeAndIsAfter_compareDates() {
        assertTrue(DateUtils.isBefore("2024-01-01", "2024-01-02"));
        assertFalse(DateUtils.isBefore("2024-01-02", "2024-01-01"));

        assertTrue(DateUtils.isAfter("2024-01-02", "2024-01-01"));
        assertFalse(DateUtils.isAfter("2024-01-01", "2024-01-02"));
    }

    @Test
    void formatRelativeTime_returnsHumanReadableString() {
        String today = DateUtils.formatRelativeTime(DateUtils.getCurrentDate());
        assertEquals("today", today);

        String tomorrow = DateUtils.formatRelativeTime(DateUtils.addDays(DateUtils.getCurrentDate(), 1));
        assertTrue(tomorrow.startsWith("in "));

        String yesterday = DateUtils.formatRelativeTime(DateUtils.addDays(DateUtils.getCurrentDate(), -1));
        assertTrue(yesterday.endsWith(" ago"));
    }
}

