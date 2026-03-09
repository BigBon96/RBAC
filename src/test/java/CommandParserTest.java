import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void testParseValidCommand() {
        Command cmd = CommandParser.parse("user-create");
        assertNotNull(cmd);
    }

    @Test
    void testParseInvalidCommand() {
        Command cmd = CommandParser.parse("invalid-command");
        assertNull(cmd);
    }

    @Test
    void testParseCaseInsensitive() {
        Command cmd1 = CommandParser.parse("USER-CREATE");
        Command cmd2 = CommandParser.parse("user-create");
        assertNotNull(cmd1);
        assertNotNull(cmd2);
    }

    @Test
    void testPrintHelp() {
        // Просто проверяем, что метод не выбрасывает исключение
        assertDoesNotThrow(() -> CommandParser.printHelp());
    }
}


