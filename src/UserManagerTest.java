public class UserManagerTest {
    public static void main(String[] args) {
        UserManagerTest test = new UserManagerTest();
        test.testAdd();
        test.testRemove();
        test.testFindById();
        test.testFindByUsername();
        test.testFindByEmail();
        test.testFindByFilter();
        test.testExists();
        test.testUpdate();
        test.testClear();
        System.out.println("All UserManager tests passed!");
    }

    private void testAdd() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        assert manager.count() == 1 : "User should be added";
        assert manager.exists("testuser") : "User should exist";
    }

    private void testRemove() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        boolean removed = manager.remove(user);
        assert removed : "User should be removed";
        assert manager.count() == 0 : "Count should be 0";
    }

    private void testFindById() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        var found = manager.findById("testuser");
        assert found.isPresent() : "User should be found";
        assert found.get().equals(user) : "Found user should match";
    }

    private void testFindByUsername() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        var found = manager.findByUsername("testuser");
        assert found.isPresent() : "User should be found by username";
    }

    private void testFindByEmail() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        var found = manager.findByEmail("test@example.com");
        assert found.isPresent() : "User should be found by email";
    }

    private void testFindByFilter() {
        UserManager manager = new UserManager();
        User user1 = User.create("user1", "John Doe", "john@example.com");
        User user2 = User.create("user2", "Jane Doe", "jane@example.com");
        manager.add(user1);
        manager.add(user2);
        var filtered = manager.findByFilter(UserFilters.byEmailDomain("@example.com"));
        assert filtered.size() == 2 : "Should find 2 users";
    }

    private void testExists() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        assert manager.exists("testuser") : "User should exist";
        assert !manager.exists("nonexistent") : "Non-existent user should not exist";
    }

    private void testUpdate() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        manager.update("testuser", "Updated Name", "updated@example.com");
        var updated = manager.findByUsername("testuser");
        assert updated.isPresent() : "User should exist after update";
        assert updated.get().fullName().equals("Updated Name") : "Full name should be updated";
        assert updated.get().email().equals("updated@example.com") : "Email should be updated";
    }

    private void testClear() {
        UserManager manager = new UserManager();
        User user = User.create("testuser", "Test User", "test@example.com");
        manager.add(user);
        manager.clear();
        assert manager.count() == 0 : "Count should be 0 after clear";
    }
}

