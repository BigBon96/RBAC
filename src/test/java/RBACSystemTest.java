import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RBACSystemTest {

    RBACSystem system;

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
    }

    @Test
    void testInitialization() {
        system.initialize();
        assertTrue(system.getUserManager().exists("admin"));
        assertTrue(system.getRoleManager().exists("Administrator"));
        assertTrue(system.getRoleManager().exists("Manager"));
        assertTrue(system.getRoleManager().exists("User"));
    }

    @Test
    void testGetManagers() {
        assertNotNull(system.getUserManager());
        assertNotNull(system.getRoleManager());
        assertNotNull(system.getAssignmentManager());
    }

    @Test
    void testCurrentUser() {
        assertEquals("system", system.getCurrentUser());
        system.setCurrentUser("admin");
        assertEquals("admin", system.getCurrentUser());
    }

    @Test
    void testGenerateStatistics() {
        system.initialize();
        String stats = system.generateStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Пользователей"));
        assertTrue(stats.contains("Ролей"));
        assertTrue(stats.contains("Назначений"));
    }

    @Test
    void auditLogIsAvailable() {
        assertNotNull(system.getAuditLog());
    }
}

