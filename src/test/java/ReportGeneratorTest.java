import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager(userManager, roleManager);

        User u1 = User.create("alice", "Alice", "alice@mail.com");
        User u2 = User.create("bob", "Bob", "bob@mail.com");
        userManager.add(u1);
        userManager.add(u2);

        Role r1 = new Role("Admin", "Administrator role");
        Role r2 = new Role("User", "User role");
        roleManager.add(r1);
        roleManager.add(r2);

        AssignmentMetadata m = AssignmentMetadata.now("system", "test");
        assignmentManager.add(new PermanentAssignment(u1, r1, m));
        assignmentManager.add(new PermanentAssignment(u2, r2, m));
    }

    @Test
    void generateUserReport_containsUsersAndRoles() {
        String report = ReportGenerator.generateUserReport(userManager, assignmentManager);
        assertTrue(report.contains("alice"));
        assertTrue(report.contains("Admin"));
        assertTrue(report.contains("bob"));
        assertTrue(report.contains("User"));
    }

    @Test
    void generateRoleReport_containsRolesAndUserCounts() {
        String report = ReportGenerator.generateRoleReport(roleManager, assignmentManager);
        assertTrue(report.contains("Admin"));
        assertTrue(report.contains("User"));
        assertTrue(report.contains("1")); // по одному пользователю на роль
    }

    @Test
    void generatePermissionMatrix_containsUsersAndResources() {
        // добавим права, чтобы появились ресурсы
        Role adminRole = roleManager.findByName("Admin").orElseThrow();
        Role userRole = roleManager.findByName("User").orElseThrow();
        Permission pUsers = new Permission("READ", "users", "Read users");
        Permission pReports = new Permission("READ", "reports", "Read reports");
        adminRole.addPermission(pUsers);
        adminRole.addPermission(pReports);
        userRole.addPermission(pUsers);

        String report = ReportGenerator.generatePermissionMatrix(userManager, assignmentManager);
        assertTrue(report.contains("alice"));
        assertTrue(report.contains("bob"));
        assertTrue(report.contains("users"));
        assertTrue(report.contains("reports"));
    }
}

