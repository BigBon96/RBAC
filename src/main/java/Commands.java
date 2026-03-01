import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Commands {
    
    // ========== Команды пользователей ==========
    
    public static void createUser(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Введите email: ");
        String email = scanner.nextLine().trim();
        
        try {
            User user = User.create(username, fullName, email);
            system.getUserManager().add(user);
            System.out.println("Пользователь создан: " + user.format());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void updateUser(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username для обновления: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите новое full name: ");
        String newFullName = scanner.nextLine().trim();
        System.out.print("Введите новый email: ");
        String newEmail = scanner.nextLine().trim();
        
        try {
            system.getUserManager().update(username, newFullName, newEmail);
            System.out.println("Пользователь обновлён");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void deleteUser(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username для удаления: ");
        String username = scanner.nextLine().trim();
        
        Optional<User> userOpt = system.getUserManager().findByUsername(username);
        if (userOpt.isPresent()) {
            boolean removed = system.getUserManager().remove(userOpt.get());
            if (removed) {
                System.out.println("Пользователь удалён");
            }
        } else {
            System.out.println("Пользователь не найден");
        }
    }
    
    public static void listUsers(Scanner scanner, RBACSystem system) {
        List<User> users = system.getUserManager().findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет");
        } else {
            System.out.println("Список пользователей:");
            users.forEach(u -> System.out.println("  " + u.format()));
        }
    }
    
    public static void searchUsers(Scanner scanner, RBACSystem system) {
        System.out.println("Выберите фильтр:");
        System.out.println("1. По username");
        System.out.println("2. По username (содержит)");
        System.out.println("3. По email");
        System.out.println("4. По домену email");
        System.out.println("5. По full name (содержит)");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        
        UserFilter filter = null;
        switch (choice) {
            case "1":
                System.out.print("Введите username: ");
                filter = UserFilters.byUsername(scanner.nextLine().trim());
                break;
            case "2":
                System.out.print("Введите подстроку: ");
                filter = UserFilters.byUsernameContains(scanner.nextLine().trim());
                break;
            case "3":
                System.out.print("Введите email: ");
                filter = UserFilters.byEmail(scanner.nextLine().trim());
                break;
            case "4":
                System.out.print("Введите домен (например, @company.com): ");
                filter = UserFilters.byEmailDomain(scanner.nextLine().trim());
                break;
            case "5":
                System.out.print("Введите подстроку: ");
                filter = UserFilters.byFullNameContains(scanner.nextLine().trim());
                break;
            default:
                System.out.println("Неверный выбор");
                return;
        }
        
        List<User> results = system.getUserManager().findByFilter(filter);
        if (results.isEmpty()) {
            System.out.println("Пользователи не найдены");
        } else {
            System.out.println("Найдено пользователей: " + results.size());
            results.forEach(u -> System.out.println("  " + u.format()));
        }
    }
    
    // ========== Команды ролей ==========
    
    public static void createRole(Scanner scanner, RBACSystem system) {
        System.out.print("Введите название роли: ");
        String name = scanner.nextLine().trim();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine().trim();
        
        try {
            Role role = new Role(name, description);
            system.getRoleManager().add(role);
            System.out.println("Роль создана: " + role.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void deleteRole(Scanner scanner, RBACSystem system) {
        System.out.print("Введите название роли для удаления: ");
        String roleName = scanner.nextLine().trim();
        
        Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
        if (roleOpt.isPresent()) {
            // Проверка на назначенность
            List<RoleAssignment> assignments = system.getAssignmentManager().findByRole(roleOpt.get());
            if (!assignments.isEmpty()) {
                System.out.println("Ошибка: роль назначена " + assignments.size() + " пользователям. Сначала отзовите назначения.");
                return;
            }
            boolean removed = system.getRoleManager().remove(roleOpt.get());
            if (removed) {
                System.out.println("Роль удалена");
            }
        } else {
            System.out.println("Роль не найдена");
        }
    }
    
    public static void listRoles(Scanner scanner, RBACSystem system) {
        List<Role> roles = system.getRoleManager().findAll();
        if (roles.isEmpty()) {
            System.out.println("Ролей нет");
        } else {
            System.out.println("Список ролей:");
            roles.forEach(r -> System.out.println("  " + r.format()));
        }
    }
    
    public static void addPermissionToRole(Scanner scanner, RBACSystem system) {
        System.out.print("Введите название роли: ");
        String roleName = scanner.nextLine().trim();
        System.out.print("Введите название права (например, READ): ");
        String permName = scanner.nextLine().trim();
        System.out.print("Введите ресурс (например, users): ");
        String resource = scanner.nextLine().trim();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine().trim();
        
        try {
            Permission permission = new Permission(permName, resource, description);
            system.getRoleManager().addPermissionToRole(roleName, permission);
            System.out.println("Право добавлено к роли");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void removePermissionFromRole(Scanner scanner, RBACSystem system) {
        System.out.print("Введите название роли: ");
        String roleName = scanner.nextLine().trim();
        System.out.print("Введите название права: ");
        String permName = scanner.nextLine().trim();
        System.out.print("Введите ресурс: ");
        String resource = scanner.nextLine().trim();
        
        try {
            Permission permission = new Permission(permName, resource, "temp");
            system.getRoleManager().removePermissionFromRole(roleName, permission);
            System.out.println("Право удалено из роли");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void searchRoles(Scanner scanner, RBACSystem system) {
        System.out.println("Выберите фильтр:");
        System.out.println("1. По названию");
        System.out.println("2. По названию (содержит)");
        System.out.println("3. По праву");
        System.out.println("4. Минимум N прав");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        
        RoleFilter filter = null;
        switch (choice) {
            case "1":
                System.out.print("Введите название: ");
                filter = RoleFilters.byName(scanner.nextLine().trim());
                break;
            case "2":
                System.out.print("Введите подстроку: ");
                filter = RoleFilters.byNameContains(scanner.nextLine().trim());
                break;
            case "3":
                System.out.print("Введите название права: ");
                String permName = scanner.nextLine().trim();
                System.out.print("Введите ресурс: ");
                String resource = scanner.nextLine().trim();
                filter = RoleFilters.hasPermission(permName, resource);
                break;
            case "4":
                System.out.print("Введите минимальное количество прав: ");
                try {
                    int n = Integer.parseInt(scanner.nextLine().trim());
                    filter = RoleFilters.hasAtLeastNPermissions(n);
                } catch (NumberFormatException e) {
                    System.out.println("Неверное число");
                    return;
                }
                break;
            default:
                System.out.println("Неверный выбор");
                return;
        }
        
        List<Role> results = system.getRoleManager().findByFilter(filter);
        if (results.isEmpty()) {
            System.out.println("Роли не найдены");
        } else {
            System.out.println("Найдено ролей: " + results.size());
            results.forEach(r -> System.out.println("  " + r.format()));
        }
    }
    
    // ========== Команды назначений ==========
    
    public static void createAssignment(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите название роли: ");
        String roleName = scanner.nextLine().trim();
        System.out.print("Введите тип (PERMANENT/TEMPORARY): ");
        String type = scanner.nextLine().trim().toUpperCase();
        System.out.print("Введите причину назначения: ");
        String reason = scanner.nextLine().trim();
        
        Optional<User> userOpt = system.getUserManager().findByUsername(username);
        Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
        
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }
        if (roleOpt.isEmpty()) {
            System.out.println("Роль не найдена");
            return;
        }
        
        AssignmentMetadata metadata = AssignmentMetadata.now(system.getCurrentUser(), reason);
        RoleAssignment assignment;
        
        if ("TEMPORARY".equals(type)) {
            System.out.print("Введите дату истечения (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            try {
                String expiresAt = LocalDate.parse(dateStr).atStartOfDay().toString();
                TemporaryAssignment temp = new TemporaryAssignment(userOpt.get(), roleOpt.get(), metadata);
                temp.extend(expiresAt);
                assignment = temp;
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты");
                return;
            }
        } else {
            assignment = new PermanentAssignment(userOpt.get(), roleOpt.get(), metadata);
        }
        
        try {
            system.getAssignmentManager().add(assignment);
            System.out.println("Назначение создано");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void revokeAssignment(Scanner scanner, RBACSystem system) {
        System.out.print("Введите assignment ID или username: ");
        String input = scanner.nextLine().trim();
        
        RoleAssignment assignment = null;
        if (input.startsWith("assignment_")) {
            assignment = system.getAssignmentManager().findById(input).orElse(null);
        } else {
            System.out.print("Введите название роли: ");
            String roleName = scanner.nextLine().trim();
            Optional<User> userOpt = system.getUserManager().findByUsername(input);
            Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
            
            if (userOpt.isPresent() && roleOpt.isPresent()) {
                List<RoleAssignment> assignments = system.getAssignmentManager().findByUser(userOpt.get());
                assignment = assignments.stream()
                    .filter(a -> a.role().equals(roleOpt.get()) && a.isActive())
                    .findFirst()
                    .orElse(null);
            }
        }
        
        if (assignment != null) {
            try {
                system.getAssignmentManager().revokeAssignment(assignment.assignmentId());
                System.out.println("Назначение отозвано");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        } else {
            System.out.println("Назначение не найдено");
        }
    }
    
    public static void listAssignments(Scanner scanner, RBACSystem system) {
        List<RoleAssignment> assignments = system.getAssignmentManager().findAll();
        if (assignments.isEmpty()) {
            System.out.println("Назначений нет");
        } else {
            System.out.println("Список назначений:");
            assignments.forEach(a -> System.out.println("  " + a.summary()));
        }
    }
    
    public static void listActiveAssignments(Scanner scanner, RBACSystem system) {
        List<RoleAssignment> assignments = system.getAssignmentManager().getActiveAssignments();
        if (assignments.isEmpty()) {
            System.out.println("Активных назначений нет");
        } else {
            System.out.println("Активные назначения:");
            assignments.forEach(a -> System.out.println("  " + a.summary()));
        }
    }
    
    public static void listExpiredAssignments(Scanner scanner, RBACSystem system) {
        List<RoleAssignment> assignments = system.getAssignmentManager().getExpiredAssignments();
        if (assignments.isEmpty()) {
            System.out.println("Истёкших назначений нет");
        } else {
            System.out.println("Истёкшие назначения:");
            assignments.forEach(a -> System.out.println("  " + a.summary()));
        }
    }
    
    public static void extendAssignment(Scanner scanner, RBACSystem system) {
        System.out.print("Введите assignment ID: ");
        String assignmentId = scanner.nextLine().trim();
        System.out.print("Введите новую дату истечения (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        try {
            String newDate = LocalDate.parse(dateStr).atStartOfDay().toString();
            system.getAssignmentManager().extendTemporaryAssignment(assignmentId, newDate);
            System.out.println("Назначение продлено");
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void searchAssignments(Scanner scanner, RBACSystem system) {
        System.out.println("Выберите фильтр:");
        System.out.println("1. По пользователю");
        System.out.println("2. По роли");
        System.out.println("3. По типу (PERMANENT/TEMPORARY)");
        System.out.println("4. По статусу (активное/неактивное)");
        System.out.println("5. Назначённые после даты");
        System.out.println("6. Истекающие до даты");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        
        AssignmentFilter filter = null;
        switch (choice) {
            case "1":
                System.out.print("Введите username: ");
                String username = scanner.nextLine().trim();
                Optional<User> userOpt = system.getUserManager().findByUsername(username);
                if (userOpt.isPresent()) {
                    filter = AssignmentFilters.byUser(userOpt.get());
                } else {
                    System.out.println("Пользователь не найден");
                    return;
                }
                break;
            case "2":
                System.out.print("Введите название роли: ");
                String roleName = scanner.nextLine().trim();
                Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                if (roleOpt.isPresent()) {
                    filter = AssignmentFilters.byRole(roleOpt.get());
                } else {
                    System.out.println("Роль не найдена");
                    return;
                }
                break;
            case "3":
                System.out.print("Введите тип (PERMANENT/TEMPORARY): ");
                filter = AssignmentFilters.byType(scanner.nextLine().trim().toUpperCase());
                break;
            case "4":
                System.out.print("Активные? (y/n): ");
                boolean active = scanner.nextLine().trim().toLowerCase().startsWith("y");
                filter = active ? AssignmentFilters.activeOnly() : AssignmentFilters.inactiveOnly();
                break;
            case "5":
                System.out.print("Введите дату (YYYY-MM-DDTHH:mm:ss): ");
                String dateAfter = scanner.nextLine().trim();
                filter = AssignmentFilters.assignedAfter(dateAfter);
                break;
            case "6":
                System.out.print("Введите дату (YYYY-MM-DDTHH:mm:ss): ");
                String dateBefore = scanner.nextLine().trim();
                filter = AssignmentFilters.expiringBefore(dateBefore);
                break;
            default:
                System.out.println("Неверный выбор");
                return;
        }
        
        List<RoleAssignment> results = system.getAssignmentManager().findByFilter(filter);
        if (results.isEmpty()) {
            System.out.println("Назначения не найдены");
        } else {
            System.out.println("Найдено назначений: " + results.size());
            results.forEach(a -> System.out.println("  " + a.summary()));
        }
    }
    
    // ========== Команды просмотра прав ==========
    
    public static void showUserPermissions(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username: ");
        String username = scanner.nextLine().trim();
        
        Optional<User> userOpt = system.getUserManager().findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }
        
        Set<Permission> permissions = system.getAssignmentManager().getUserPermissions(userOpt.get());
        if (permissions.isEmpty()) {
            System.out.println("У пользователя нет прав");
        } else {
            System.out.println("Права пользователя " + username + ":");
            Map<String, List<Permission>> byResource = permissions.stream()
                .collect(Collectors.groupingBy(Permission::resource));
            
            byResource.forEach((resource, perms) -> {
                System.out.println("  " + resource + ":");
                perms.forEach(p -> System.out.println("    - " + p.format()));
            });
        }
    }
    
    public static void checkPermission(Scanner scanner, RBACSystem system) {
        System.out.print("Введите username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите название права: ");
        String permName = scanner.nextLine().trim();
        System.out.print("Введите ресурс: ");
        String resource = scanner.nextLine().trim();
        
        Optional<User> userOpt = system.getUserManager().findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }
        
        boolean hasPermission = system.getAssignmentManager().userHasPermission(userOpt.get(), permName, resource);
        if (hasPermission) {
            System.out.println("Пользователь имеет право " + permName + " на " + resource);
            // Найти из какой роли
            List<RoleAssignment> assignments = system.getAssignmentManager().findByUser(userOpt.get());
            for (RoleAssignment assignment : assignments) {
                if (assignment.isActive() && assignment.role().hasPermission(permName, resource)) {
                    System.out.println("  (из роли: " + assignment.role().getName() + ")");
                    break;
                }
            }
        } else {
            System.out.println("Пользователь НЕ имеет право " + permName + " на " + resource);
        }
    }
    
    // ========== Служебные команды ==========
    
    public static void showHelp(Scanner scanner, RBACSystem system) {
        CommandParser.printHelp();
    }
    
    public static void showStats(Scanner scanner, RBACSystem system) {
        System.out.println(system.generateStatistics());
    }
    
    public static void clearScreen(Scanner scanner, RBACSystem system) {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    public static void exit(Scanner scanner, RBACSystem system) {
        System.out.print("Сохранить данные перед выходом? (y/n): ");
        String save = scanner.nextLine().trim().toLowerCase();
        if (save.startsWith("y")) {
            save(scanner, system);
        }
        System.out.println("Выход из программы");
        System.exit(0);
    }
    
    public static void save(Scanner scanner, RBACSystem system) {
        System.out.print("Введите имя файла (по умолчанию data.txt): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "data.txt";
        }
        
        // Простая реализация сохранения (можно улучшить)
        System.out.println("Сохранение данных в " + filename + "...");
        System.out.println("(Функция сохранения требует дополнительной реализации)");
    }
    
    public static void load(Scanner scanner, RBACSystem system) {
        System.out.print("Введите имя файла: ");
        String filename = scanner.nextLine().trim();
        
        // Простая реализация загрузки (можно улучшить)
        System.out.println("Загрузка данных из " + filename + "...");
        System.out.println("(Функция загрузки требует дополнительной реализации)");
    }
}

