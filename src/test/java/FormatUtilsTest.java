import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FormatUtilsTest {

    @Test
    void formatTable_buildsAsciiTable() {
        String[] headers = {"Username", "Email"};
        List<String[]> rows = List.of(
                new String[]{"admin", "admin@system.com"},
                new String[]{"user", "user@mail.com"}
        );

        String table = FormatUtils.formatTable(headers, rows);

        assertTrue(table.contains("Username"));
        assertTrue(table.contains("admin@system.com"));
        assertTrue(table.startsWith("+"));
        assertTrue(table.contains("| admin "));
    }

    @Test
    void formatBox_wrapsTextWithBorder() {
        String box = FormatUtils.formatBox("Hello");
        assertTrue(box.contains("Hello"));
        assertTrue(box.startsWith("+"));
        assertTrue(box.endsWith("+"));
    }

    @Test
    void formatHeader_addsUnderline() {
        String header = FormatUtils.formatHeader("Title");
        assertTrue(header.startsWith("Title"));
        assertTrue(header.contains(System.lineSeparator()));
        String[] lines = header.split("\\R");
        assertEquals(2, lines.length);
        assertEquals("-----", lines[1]);
    }

    @Test
    void truncate_shorterThanLimitReturnsOriginal() {
        assertEquals("abc", FormatUtils.truncate("abc", 5));
    }

    @Test
    void truncate_longerThanLimitAddsEllipsis() {
        String result = FormatUtils.truncate("abcdefgh", 5);
        assertEquals("ab...", result);
    }

    @Test
    void padRight_extendsToLength() {
        assertEquals("abc  ", FormatUtils.padRight("abc", 5));
        assertEquals("", FormatUtils.padRight("abc", 0));
    }

    @Test
    void padLeft_extendsToLength() {
        assertEquals("  abc", FormatUtils.padLeft("abc", 5));
        assertEquals("", FormatUtils.padLeft("abc", 0));
    }
}

