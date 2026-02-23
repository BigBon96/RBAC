public class RoleManagerTest {
    public static void main(String[] args) {
        RoleManagerTest test = new RoleManagerTest();
        test.testAdd();
        test.testRemove();
        test.testFindById();
        test.testFindByName();
        test.testFindByFilter();
        test.testExists();
        test.testAddPermission();
        test.testRemovePermission();
        test.testFindRolesWithPermission();
        test.testClear();
        System.out.println("All RoleManager tests passed!");
    }

    private void testAdd() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        assert manager.count() == 1 : "Role should be added";
        assert manager.exists("Admin") : "Role should exist";
    }

    private void testRemove() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        boolean removed = manager.remove(role);
        assert removed : "Role should be removed";
        assert manager.count() == 0 : "Count should be 0";
    }

    private void testFindById() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        var found = manager.findById(role.getId());
        assert found.isPresent() : "Role should be found";
        assert found.get().equals(role) : "Found role should match";
    }

    private void testFindByName() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        var found = manager.findByName("Admin");
        assert found.isPresent() : "Role should be found by name";
    }

    private void testFindByFilter() {
        RoleManager manager = new RoleManager();
        Role role1 = new Role("Admin", "Administrator");
        Role role2 = new Role("User", "Regular user");
        manager.add(role1);
        manager.add(role2);
        var filtered = manager.findByFilter(RoleFilters.byNameContains("Admin"));
        assert filtered.size() == 1 : "Should find 1 role";
    }

    private void testExists() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        assert manager.exists("Admin") : "Role should exist";
        assert !manager.exists("Nonexistent") : "Non-existent role should not exist";
    }

    private void testAddPermission() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        Permission perm = new Permission("READ", "users", "Read users");
        manager.addPermissionToRole("Admin", perm);
        assert role.hasPermission(perm) : "Role should have permission";
    }

    private void testRemovePermission() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        Permission perm = new Permission("READ", "users", "Read users");
        role.addPermission(perm);
        manager.add(role);
        manager.removePermissionFromRole("Admin", perm);
        assert !role.hasPermission(perm) : "Role should not have permission";
    }

    private void testFindRolesWithPermission() {
        RoleManager manager = new RoleManager();
        Role role1 = new Role("Admin", "Administrator");
        Permission perm = new Permission("READ", "users", "Read users");
        role1.addPermission(perm);
        manager.add(role1);
        var roles = manager.findRolesWithPermission("READ", "users");
        assert roles.size() == 1 : "Should find 1 role with permission";
    }

    private void testClear() {
        RoleManager manager = new RoleManager();
        Role role = new Role("Admin", "Administrator role");
        manager.add(role);
        manager.clear();
        assert manager.count() == 0 : "Count should be 0 after clear";
    }
}
