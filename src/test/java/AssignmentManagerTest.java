import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentManagerTest {

    UserManager userManager;
    RoleManager roleManager;
    AssignmentManager manager;
    User user1;
    User user2;
    Role role1;
    Role role2;
    Permission perm1;
    AssignmentMetadata metadata;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        roleManager = new RoleManager();
        manager = new AssignmentManager(userManager, roleManager);
        
        user1 = User.create("john_doe", "John Doe", "john@mail.com");
        user2 = User.create("jane_admin", "Jane Admin", "jane@mail.com");
        userManager.add(user1);
        userManager.add(user2);
        
        role1 = new Role("admin", "Administrator");
        role2 = new Role("user", "Regular user");
        perm1 = new Permission("read", "users", "Read users");
        role1.addPermission(perm1);
        roleManager.add(role1);
        roleManager.add(role2);
        
        metadata = AssignmentMetadata.now("system", "Test assignment");
    }

    @Test
    void addAndFindById() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        assertEquals(assignment, manager.findById(assignment.assignmentId()).orElse(null));
        assertEquals(1, manager.count());
    }

    @Test
    void addWithNonExistentUserThrows() {
        var nonExistentUser = User.create("nonexistent", "Non Existent", "none@mail.com");
        var assignment = new PermanentAssignment(nonExistentUser, role1, metadata);
        assertThrows(IllegalArgumentException.class, () -> manager.add(assignment));
    }

    @Test
    void addWithNonExistentRoleThrows() {
        var nonExistentRole = new Role("nonexistent", "Non existent role");
        var assignment = new PermanentAssignment(user1, nonExistentRole, metadata);
        assertThrows(IllegalArgumentException.class, () -> manager.add(assignment));
    }

    @Test
    void addDuplicateAssignmentThrows() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        var duplicate = new PermanentAssignment(user1, role1, metadata);
        assertThrows(IllegalArgumentException.class, () -> manager.add(duplicate));
    }

    @Test
    void remove() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        assertTrue(manager.remove(assignment));
        assertTrue(manager.findById(assignment.assignmentId()).isEmpty());
    }

    @Test
    void findByUser() {
        var assignment1 = new PermanentAssignment(user1, role1, metadata);
        var assignment2 = new PermanentAssignment(user1, role2, metadata);
        manager.add(assignment1);
        manager.add(assignment2);
        var list = manager.findByUser(user1);
        assertEquals(2, list.size());
    }

    @Test
    void findByRole() {
        var assignment1 = new PermanentAssignment(user1, role1, metadata);
        var assignment2 = new PermanentAssignment(user2, role1, metadata);
        manager.add(assignment1);
        manager.add(assignment2);
        var list = manager.findByRole(role1);
        assertEquals(2, list.size());
    }

    @Test
    void getActiveAssignments() {
        var activeAssignment = new PermanentAssignment(user1, role1, metadata);
        var expiredAssignment = new TemporaryAssignment(user2, role1, metadata);
        expiredAssignment.extend(LocalDate.parse("2010-01-01").atStartOfDay().toString());
        manager.add(activeAssignment);
        manager.add(expiredAssignment);
        var active = manager.getActiveAssignments();
        assertEquals(1, active.size());
    }

    @Test
    void getExpiredAssignments() {
        var activeAssignment = new PermanentAssignment(user1, role1, metadata);
        var expiredAssignment = new TemporaryAssignment(user2, role1, metadata);
        expiredAssignment.extend(LocalDate.parse("2010-01-01").atStartOfDay().toString());
        manager.add(activeAssignment);
        manager.add(expiredAssignment);
        var expired = manager.getExpiredAssignments();
        assertEquals(1, expired.size());
    }

    @Test
    void userHasRole() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        assertTrue(manager.userHasRole(user1, role1));
        assertFalse(manager.userHasRole(user1, role2));
    }

    @Test
    void userHasPermission() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        assertTrue(manager.userHasPermission(user1, "read", "users"));
        assertFalse(manager.userHasPermission(user1, "write", "users"));
    }

    @Test
    void getUserPermissions() {
        var perm2 = new Permission("write", "users", "Write users");
        role1.addPermission(perm2);
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        var permissions = manager.getUserPermissions(user1);
        assertEquals(2, permissions.size());
        assertTrue(permissions.stream().anyMatch(p -> p.matches("read", "users")));
        assertTrue(permissions.stream().anyMatch(p -> p.matches("write", "users")));
    }

    @Test
    void findByFilter() {
        var assignment1 = new PermanentAssignment(user1, role1, metadata);
        var assignment2 = new TemporaryAssignment(user2, role1, metadata);
        assignment2.extend(LocalDate.parse("2040-01-01").atStartOfDay().toString());
        manager.add(assignment1);
        manager.add(assignment2);
        var list = manager.findByFilter(AssignmentFilters.activeOnly());
        assertEquals(2, list.size());
    }

    @Test
    void findAllWithFilterAndSorter() {
        var assignment1 = new PermanentAssignment(user1, role1, metadata);
        var assignment2 = new PermanentAssignment(user2, role1, metadata);
        manager.add(assignment1);
        manager.add(assignment2);
        var list = manager.findAll(AssignmentFilters.activeOnly(), AssignmentSorters.byUsername());
        assertEquals(2, list.size());
    }

    @Test
    void revokeAssignment() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        manager.revokeAssignment(assignment.assignmentId());
        assertFalse(assignment.isActive());
    }

    @Test
    void extendTemporaryAssignment() {
        var assignment = new TemporaryAssignment(user1, role1, metadata);
        assignment.extend(LocalDate.parse("2025-01-01").atStartOfDay().toString());
        manager.add(assignment);
        String newDate = LocalDate.parse("2026-01-01").atStartOfDay().toString();
        manager.extendTemporaryAssignment(assignment.assignmentId(), newDate);
        assertEquals(newDate, assignment.getExpiresAt());
    }

    @Test
    void extendPermanentAssignmentThrows() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        assertThrows(IllegalArgumentException.class, 
            () -> manager.extendTemporaryAssignment(assignment.assignmentId(), "2026-01-01T00:00:00"));
    }

    @Test
    void clear() {
        var assignment = new PermanentAssignment(user1, role1, metadata);
        manager.add(assignment);
        manager.clear();
        assertEquals(0, manager.count());
    }
}
