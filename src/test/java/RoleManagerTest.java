import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleManagerTest {

    RoleManager manager;
    Role role1;
    Role role2;
    Permission perm1;
    Permission perm2;

    @BeforeEach
    void setUp() {
        manager = new RoleManager();
        role1 = new Role("admin", "Administrator role");
        role2 = new Role("user", "Regular user");
        perm1 = new Permission("read", "users", "Read users");
        perm2 = new Permission("write", "users", "Write users");
    }

    @Test
    void addAndFindById() {
        manager.add(role1);
        assertEquals(role1, manager.findById(role1.getId()).orElse(null));
        assertEquals(1, manager.count());
    }

    @Test
    void addDuplicateThrows() {
        manager.add(role1);
        assertThrows(IllegalArgumentException.class, () -> manager.add(role1));
    }

    @Test
    void remove() {
        manager.add(role1);
        assertTrue(manager.remove(role1));
        assertTrue(manager.findById(role1.getId()).isEmpty());
        assertEquals(0, manager.count());
    }

    @Test
    void findByName() {
        manager.add(role1);
        assertEquals(role1, manager.findByName("admin").orElse(null));
    }

    @Test
    void exists() {
        manager.add(role1);
        assertTrue(manager.exists("admin"));
        assertFalse(manager.exists("nonexistent"));
    }

    @Test
    void findByFilter() {
        manager.add(role1);
        manager.add(role2);
        var list = manager.findByFilter(RoleFilters.byNameContains("admin"));
        assertEquals(1, list.size());
        assertEquals("admin", list.get(0).getName());
    }

    @Test
    void findAllWithFilterAndSorter() {
        role1.addPermission(perm1);
        role1.addPermission(perm2);
        role2.addPermission(perm1);
        manager.add(role1);
        manager.add(role2);
        var list = manager.findAll(RoleFilters.hasAtLeastNPermissions(1), RoleSorters.byPermissionCount());
        assertEquals(2, list.size());
    }

    @Test
    void addPermissionToRole() {
        manager.add(role1);
        manager.addPermissionToRole("admin", perm1);
        assertTrue(role1.hasPermission(perm1));
    }

    @Test
    void removePermissionFromRole() {
        role1.addPermission(perm1);
        manager.add(role1);
        manager.removePermissionFromRole("admin", perm1);
        assertFalse(role1.hasPermission(perm1));
    }

    @Test
    void findRolesWithPermission() {
        role1.addPermission(perm1);
        manager.add(role1);
        manager.add(role2);
        var list = manager.findRolesWithPermission("read", "users");
        assertEquals(1, list.size());
        assertEquals("admin", list.get(0).getName());
    }

    @Test
    void clear() {
        manager.add(role1);
        manager.clear();
        assertEquals(0, manager.count());
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleManagerTest {

    RoleManager manager;
    Role role1;
    Role role2;
    Permission perm1;
    Permission perm2;

    @BeforeEach
    void setUp() {
        manager = new RoleManager();
        role1 = new Role("admin", "Administrator role");
        role2 = new Role("user", "Regular user");
        perm1 = new Permission("read", "users", "Read users");
        perm2 = new Permission("write", "users", "Write users");
    }

    @Test
    void addAndFindById() {
        manager.add(role1);
        assertEquals(role1, manager.findById(role1.getId()).orElse(null));
        assertEquals(1, manager.count());
    }

    @Test
    void addDuplicateThrows() {
        manager.add(role1);
        assertThrows(IllegalArgumentException.class, () -> manager.add(role1));
    }

    @Test
    void remove() {
        manager.add(role1);
        assertTrue(manager.remove(role1));
        assertTrue(manager.findById(role1.getId()).isEmpty());
        assertEquals(0, manager.count());
    }

    @Test
    void findByName() {
        manager.add(role1);
        assertEquals(role1, manager.findByName("admin").orElse(null));
    }

    @Test
    void exists() {
        manager.add(role1);
        assertTrue(manager.exists("admin"));
        assertFalse(manager.exists("nonexistent"));
    }

    @Test
    void findByFilter() {
        manager.add(role1);
        manager.add(role2);
        var list = manager.findByFilter(RoleFilters.byNameContains("admin"));
        assertEquals(1, list.size());
        assertEquals("admin", list.get(0).getName());
    }

    @Test
    void findAllWithFilterAndSorter() {
        role1.addPermission(perm1);
        role1.addPermission(perm2);
        role2.addPermission(perm1);
        manager.add(role1);
        manager.add(role2);
        var list = manager.findAll(RoleFilters.hasAtLeastNPermissions(1), RoleSorters.byPermissionCount());
        assertEquals(2, list.size());
    }

    @Test
    void addPermissionToRole() {
        manager.add(role1);
        manager.addPermissionToRole("admin", perm1);
        assertTrue(role1.hasPermission(perm1));
    }

    @Test
    void removePermissionFromRole() {
        role1.addPermission(perm1);
        manager.add(role1);
        manager.removePermissionFromRole("admin", perm1);
        assertFalse(role1.hasPermission(perm1));
    }

    @Test
    void findRolesWithPermission() {
        role1.addPermission(perm1);
        manager.add(role1);
        manager.add(role2);
        var list = manager.findRolesWithPermission("read", "users");
        assertEquals(1, list.size());
        assertEquals("admin", list.get(0).getName());
    }

    @Test
    void clear() {
        manager.add(role1);
        manager.clear();
        assertEquals(0, manager.count());
    }
}
