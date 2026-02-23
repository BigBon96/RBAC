import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Демонстрация работоспособности всех подпунктов ===\n");

        // Базовые модели
        System.out.println("--- Базовые модели ---");
        User user = new User("username", "full name", "email@mail.ru");
        User user2 = new User("operator", "full name", "email@mail.ru");
        User user3 = new User("admin_user", "Admin User", "admin@company.com");
        
        Role role = new Role("admin", "admin permissions");
        Permission p1 = new Permission("read", "users", "can read users");
        Permission p2 = new Permission("delete", "users", "can delete users");
        role.addPermission(p1);
        role.addPermission(p2);
        
        AssignmentMetadata am = AssignmentMetadata.now(user2.username(), "with some reason");

        TemporaryAssignment ta = new TemporaryAssignment(user, role, am);
        ta.extend(LocalDate.parse("2040-02-20").atStartOfDay().toString());
        System.out.println("TemporaryAssignment (будущее):");
        System.out.println(ta.summary());
        
        ta.extend(LocalDate.parse("2010-02-20").atStartOfDay().toString());
        System.out.println("\nTemporaryAssignment (прошлое - истекшее):");
        System.out.println(ta.summary());

        PermanentAssignment pa = new PermanentAssignment(user, role, am);
        System.out.println("\nPermanentAssignment (активное):");
        System.out.println(pa.summary());
        pa.revoke();
        System.out.println("\nPermanentAssignment (отозванное):");
        System.out.println(pa.summary());

        // Подзадача 2.1: UserFilter
        System.out.println("\n=== Подзадача 2.1: UserFilter ===");
        List<User> users = List.of(user, user2, user3);
        
        System.out.println("\nФильтр byUsername('username'):");
        users.stream().filter(UserFilters.byUsername("username")).forEach(u -> System.out.println("  " + u.format()));
        
        System.out.println("\nФильтр byUsernameContains('admin'):");
        users.stream().filter(UserFilters.byUsernameContains("admin")).forEach(u -> System.out.println("  " + u.format()));
        
        System.out.println("\nФильтр byEmailDomain('@company.com'):");
        users.stream().filter(UserFilters.byEmailDomain("@company.com")).forEach(u -> System.out.println("  " + u.format()));
        
        System.out.println("\nФильтр byFullNameContains('Admin'):");
        users.stream().filter(UserFilters.byFullNameContains("Admin")).forEach(u -> System.out.println("  " + u.format()));
        
        System.out.println("\nКомбинированный фильтр (byUsernameContains OR byEmailDomain):");
        UserFilter combined = UserFilters.byUsernameContains("admin").or(UserFilters.byEmailDomain("@company.com"));
        users.stream().filter(combined).forEach(u -> System.out.println("  " + u.format()));

        // Подзадача 2.2: RoleFilter
        System.out.println("\n=== Подзадача 2.2: RoleFilter ===");
        Role role2 = new Role("user", "regular user");
        role2.addPermission(p1);
        List<Role> roles = List.of(role, role2);
        
        System.out.println("\nФильтр byName('admin'):");
        roles.stream().filter(RoleFilters.byName("admin")).forEach(r -> System.out.println("  " + r.getName()));
        
        System.out.println("\nФильтр byNameContains('user'):");
        roles.stream().filter(RoleFilters.byNameContains("user")).forEach(r -> System.out.println("  " + r.getName()));
        
        System.out.println("\nФильтр hasPermission(p1):");
        roles.stream().filter(RoleFilters.hasPermission(p1)).forEach(r -> System.out.println("  " + r.getName()));
        
        System.out.println("\nФильтр hasPermission('read', 'users'):");
        roles.stream().filter(RoleFilters.hasPermission("read", "users")).forEach(r -> System.out.println("  " + r.getName()));
        
        System.out.println("\nФильтр hasAtLeastNPermissions(2):");
        roles.stream().filter(RoleFilters.hasAtLeastNPermissions(2)).forEach(r -> System.out.println("  " + r.getName() + " (" + r.getPermissions().size() + " permissions)"));

        // Подзадача 2.3: AssignmentFilter
        System.out.println("\n=== Подзадача 2.3: AssignmentFilter ===");
        TemporaryAssignment ta2 = new TemporaryAssignment(user2, role, am);
        ta2.extend(LocalDate.parse("2040-02-20").atStartOfDay().toString());
        PermanentAssignment pa2 = new PermanentAssignment(user3, role2, am);
        List<RoleAssignment> assignments = List.of(ta, ta2, pa, pa2);
        
        System.out.println("\nФильтр byUser(user):");
        assignments.stream().filter(AssignmentFilters.byUser(user)).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName()));
        
        System.out.println("\nФильтр byUsername('operator'):");
        assignments.stream().filter(AssignmentFilters.byUsername("operator")).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName()));
        
        System.out.println("\nФильтр byRole(role):");
        assignments.stream().filter(AssignmentFilters.byRole(role)).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName()));
        
        System.out.println("\nФильтр activeOnly():");
        assignments.stream().filter(AssignmentFilters.activeOnly()).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName() + " (active: " + a.isActive() + ")"));
        
        System.out.println("\nФильтр inactiveOnly():");
        assignments.stream().filter(AssignmentFilters.inactiveOnly()).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName() + " (active: " + a.isActive() + ")"));
        
        System.out.println("\nФильтр byType('PERMANENT'):");
        assignments.stream().filter(AssignmentFilters.byType("PERMANENT")).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName() + " (" + a.assignmentType() + ")"));

        // Подзадача 2.4: Sorters
        System.out.println("\n=== Подзадача 2.4: Sorters ===");
        
        System.out.println("\nСортировка User по username:");
        users.stream().sorted(UserSorters.byUsername()).forEach(u -> System.out.println("  " + u.username()));
        
        System.out.println("\nСортировка User по fullName:");
        users.stream().sorted(UserSorters.byFullName()).forEach(u -> System.out.println("  " + u.fullName()));
        
        System.out.println("\nСортировка Role по количеству прав:");
        roles.stream().sorted(RoleSorters.byPermissionCount()).forEach(r -> System.out.println("  " + r.getName() + " (" + r.getPermissions().size() + " permissions)"));
        
        System.out.println("\nСортировка Assignment по username:");
        assignments.stream().sorted(AssignmentSorters.byUsername()).forEach(a -> System.out.println("  " + a.user().username() + " -> " + a.role().getName()));

        // Подзадача 3.1: Repository
        System.out.println("\n=== Подзадача 3.1: Repository ===");
        System.out.println("Repository интерфейс реализован в UserManager, RoleManager, AssignmentManager");

        // Подзадача 3.2: UserManager
        System.out.println("\n=== Подзадача 3.2: UserManager ===");
        UserManager userManager = new UserManager();
        userManager.add(user);
        userManager.add(user2);
        userManager.add(user3);
        
        System.out.println("\nВсего пользователей: " + userManager.count());
        System.out.println("Поиск по username 'username': " + userManager.findByUsername("username").map(User::format).orElse("не найден"));
        System.out.println("Поиск по email 'admin@company.com': " + userManager.findByEmail("admin@company.com").map(User::format).orElse("не найден"));
        
        System.out.println("\nФильтрация по домену '@company.com':");
        userManager.findByFilter(UserFilters.byEmailDomain("@company.com")).forEach(u -> System.out.println("  " + u.format()));
        
        System.out.println("\nФильтрация и сортировка (byUsernameContains + сортировка по email):");
        userManager.findAll(UserFilters.byUsernameContains("user"), UserSorters.byEmail()).forEach(u -> System.out.println("  " + u.format()));
        
        userManager.update("username", "Updated Full Name", "updated@mail.ru");
        System.out.println("\nПосле обновления пользователя 'username':");
        userManager.findByUsername("username").ifPresent(u -> System.out.println("  " + u.format()));

        // Подзадача 3.3: RoleManager
        System.out.println("\n=== Подзадача 3.3: RoleManager ===");
        RoleManager roleManager = new RoleManager();
        roleManager.add(role);
        roleManager.add(role2);
        
        System.out.println("\nВсего ролей: " + roleManager.count());
        System.out.println("Поиск по имени 'admin': " + roleManager.findByName("admin").map(Role::getName).orElse("не найдена"));
        
        System.out.println("\nФильтрация ролей с минимум 2 правами:");
        roleManager.findByFilter(RoleFilters.hasAtLeastNPermissions(2)).forEach(r -> System.out.println("  " + r.getName() + " (" + r.getPermissions().size() + " permissions)"));
        
        Permission p3 = new Permission("write", "users", "can write users");
        roleManager.addPermissionToRole("admin", p3);
        System.out.println("\nПосле добавления права 'write' к роли 'admin':");
        roleManager.findByName("admin").ifPresent(r -> System.out.println("  " + r.getName() + " имеет " + r.getPermissions().size() + " прав"));
        
        System.out.println("\nРоли с правом 'read' на 'users':");
        roleManager.findRolesWithPermission("read", "users").forEach(r -> System.out.println("  " + r.getName()));

        // Подзадача 3.4: AssignmentManager
        System.out.println("\n=== Подзадача 3.4: AssignmentManager ===");
        AssignmentManager assignmentManager = new AssignmentManager(userManager, roleManager);
        
        TemporaryAssignment ta3 = new TemporaryAssignment(user, role, am);
        ta3.extend(LocalDate.parse("2040-02-20").atStartOfDay().toString());
        PermanentAssignment pa3 = new PermanentAssignment(user2, role2, am);
        assignmentManager.add(ta3);
        assignmentManager.add(pa3);
        
        System.out.println("\nВсего назначений: " + assignmentManager.count());
        System.out.println("Активных назначений: " + assignmentManager.getActiveAssignments().size());
        
        System.out.println("\nНазначения для пользователя 'username':");
        assignmentManager.findByUser(user).forEach(a -> System.out.println("  " + a.role().getName() + " (active: " + a.isActive() + ")"));
        
        System.out.println("\nПроверка: user имеет роль 'admin'? " + assignmentManager.userHasRole(user, role));
        System.out.println("Проверка: user имеет право 'read' на 'users'? " + assignmentManager.userHasPermission(user, "read", "users"));
        
        System.out.println("\nВсе права пользователя 'username':");
        assignmentManager.getUserPermissions(user).forEach(p -> System.out.println("  " + p.format()));
        
        System.out.println("\nФильтрация активных назначений:");
        assignmentManager.findByFilter(AssignmentFilters.activeOnly()).forEach(a -> 
            System.out.println("  " + a.user().username() + " -> " + a.role().getName() + " (active: " + a.isActive() + ")"));
        
        System.out.println("\nФильтрация и сортировка назначений:");
        assignmentManager.findAll(AssignmentFilters.activeOnly(), AssignmentSorters.byUsername()).forEach(a -> 
            System.out.println("  " + a.user().username() + " -> " + a.role().getName()));

        System.out.println("\n=== Все подпункты успешно продемонстрированы! ===");
    }
}
