import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    RBACSystem system;
    Scanner scanner;
    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
        system.initialize();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void testCreateUser() {
        scanner = createScanner("testuser\nTest User\ntest@mail.com\n");
        Commands.createUser(scanner, system);
        assertTrue(system.getUserManager().exists("testuser"));
    }

    @Test
    void testListUsers() {
        scanner = createScanner("");
        Commands.listUsers(scanner, system);
        String output = outputStream.toString();
        assertTrue(output.contains("Пользователей") || output.contains("admin"));
    }

    @Test
    void testCreateRole() {
        scanner = createScanner("TestRole\nTest Description\n");
        Commands.createRole(scanner, system);
        assertTrue(system.getRoleManager().exists("TestRole"));
    }

    @Test
    void testListRoles() {
        scanner = createScanner("");
        Commands.listRoles(scanner, system);
        String output = outputStream.toString();
        assertTrue(output.contains("Ролей") || output.contains("Administrator"));
    }

    @Test
    void testShowStats() {
        scanner = createScanner("");
        Commands.showStats(scanner, system);
        String output = outputStream.toString();
        assertTrue(output.contains("Статистика") || output.contains("Пользователей"));
    }

    @Test
    void testShowHelp() {
        scanner = createScanner("");
        Commands.showHelp(scanner, system);
        String output = outputStream.toString();
        assertTrue(output.contains("Справка") || output.contains("команды"));
    }
}


