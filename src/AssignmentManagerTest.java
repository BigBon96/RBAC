public class AssignmentManagerTest {
    public static void main(String[] args) {
        AssignmentManagerTest test = new AssignmentManagerTest();
        test.testAdd();
        test.testRemove();
        test.testFindById();
        test.testFindByUser();
        test.testFindByRole();
        test.testFindByFilter();
        test.testGetActiveAssignments();
        test.testUserHasRole();
        test.testUserHasPermission();
        test.testGetUserPermissions();
        test.testRevokeAssignment();
        test.testExtendTemporaryAssignment();
        System.out.println("All AssignmentManager tests passed!");
    }

    private void testAdd() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        assert manager.count() == 1 : "Assignment should be added";
    }

    private void testRemove() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        boolean removed = manager.remove(assignment);
        assert removed : "Assignment should be removed";
        assert manager.count() == 0 : "Count should be 0";
    }

    private void testFindById() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var found = manager.findById(assignment.assignmentId());
        assert found.isPresent() : "Assignment should be found";
    }

    private void testFindByUser() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var found = manager.findByUser(user);
        assert found.size() == 1 : "Should find 1 assignment for user";
    }

    private void testFindByRole() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var found = manager.findByRole(role);
        assert found.size() == 1 : "Should find 1 assignment for role";
    }

    private void testFindByFilter() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var filtered = manager.findByFilter(AssignmentFilters.activeOnly());
        assert filtered.size() == 1 : "Should find 1 active assignment";
    }

    private void testGetActiveAssignments() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var active = manager.getActiveAssignments();
        assert active.size() == 1 : "Should have 1 active assignment";
    }

    private void testUserHasRole() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        assert manager.userHasRole(user, role) : "User should have role";
    }

    private void testUserHasPermission() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        Permission perm = new Permission("READ", "users", "Read users");
        role.addPermission(perm);
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        assert manager.userHasPermission(user, "READ", "users") : "User should have permission";
    }

    private void testGetUserPermissions() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        Permission perm = new Permission("READ", "users", "Read users");
        role.addPermission(perm);
        userManager.add(user);
        roleManager.add(role);
        
        RoleAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        var perms = manager.getUserPermissions(user);
        assert perms.size() == 1 : "User should have 1 permission";
    }

    private void testRevokeAssignment() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        PermanentAssignment assignment = new PermanentAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        manager.add(assignment);
        manager.revokeAssignment(assignment.assignmentId());
        assert !assignment.isActive() : "Assignment should be revoked";
    }

    private void testExtendTemporaryAssignment() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager manager = new AssignmentManager(userManager, roleManager);
        
        User user = User.create("testuser", "Test User", "test@example.com");
        Role role = new Role("Admin", "Administrator");
        userManager.add(user);
        roleManager.add(role);
        
        TemporaryAssignment assignment = new TemporaryAssignment(user, role, AssignmentMetadata.now("admin", "Test"));
        assignment.extend("2025-12-31T23:59:59");
        manager.add(assignment);
        manager.extendTemporaryAssignment(assignment.assignmentId(), "2026-12-31T23:59:59");
        assert assignment.getExpiresAt().equals("2026-12-31T23:59:59") : "Expiration date should be extended";
    }
}

