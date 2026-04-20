import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Commands {
    
    // ========== Команды пользователей ==========
    
    public static void createUser(Scanner scanner, RBACSystem system) {
        String username = ConsoleUtils.promptString(scanner, "Введите username: ", true);
        String fullName = ConsoleUtils.promptString(scanner, "Введите full name: ", true);
        String email = ConsoleUtils.promptString(scanner, "Введите email: ", true);
        
        try {
            User user = User.create(username, fullName, email);
            system.getUserManager().add(user);
            System.out.println("Пользователь создан: " + user.format());
            system.getAuditLog().log(
                    "USER_CREATE",
                    system.getCurrentUser(),
                    username,
                    String.format("Создан пользователь %s", user.format())
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void updateUser(Scanner scanner, RBACSystem system) {
        String username = ConsoleUtils.promptString(scanner, "Введите username для обновления: ", true);
        String newFullName = ConsoleUtils.promptString(scanner, "Введите новое full name: ", true);
        String newEmail = ConsoleUtils.promptString(scanner, "Введите новый email: ", true);
        
        try {
            system.getUserManager().update(username, newFullName, newEmail);
            System.out.println("Пользователь обновлён");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void deleteUser(Scanner scanner, RBACSystem system) {
        String username = ConsoleUtils.promptString(scanner, "Введите username для удаления: ", true);
        
        Optional<User> userOpt = system.getUserManager().findByUsername(username);
        if (userOpt.isPresent()) {
            boolean removed = system.getUserManager().remove(userOpt.get());
            if (removed) {
                System.out.println("Пользователь удалён");
                system.getAuditLog().log(
                        "USER_DELETE",
                        system.getCurrentUser(),
                        username,
                        "Пользователь удалён"
                );
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
            System.out.println(FormatUtils.formatHeader("Список пользователей"));
            String[] headers = {"Username", "Full Name", "Email"};
            List<String[]> rows = new ArrayList<>();
            for (User u : users) {
                rows.add(new String[]{u.username(), u.fullName(), u.email()});
            }
            System.out.println(FormatUtils.formatTable(headers, rows));
        }
    }
    
    public static void searchUsers(Scanner scanner, RBACSystem system) {
        System.out.println(FormatUtils.formatHeader("Поиск пользователей"));
        System.out.println("1. По username");
        System.out.println("2. По username (содержит)");
        System.out.println("3. По email");
        System.out.println("4. По домену email");
        System.out.println("5. По full name (содержит)");
        String choice = ConsoleUtils.promptString(scanner, "Ваш выбор: ", true);
        
        UserFilter filter = null;
        switch (choice) {
            case "1":
                filter = UserFilters.byUsername(
                        ConsoleUtils.promptString(scanner, "Введите username: ", true));
                break;
            case "2":
                filter = UserFilters.byUsernameContains(
                        ConsoleUtils.promptString(scanner, "Введите подстроку: ", true));
                break;
            case "3":
                filter = UserFilters.byEmail(
                        ConsoleUtils.promptString(scanner, "Введите email: ", true));
                break;
            case "4":
                filter = UserFilters.byEmailDomain(
                        ConsoleUtils.promptString(scanner, "Введите домен (например, @company.com): ", true));
                break;
            case "5":
                filter = UserFilters.byFullNameContains(
                        ConsoleUtils.promptString(scanner, "Введите подстроку: ", true));
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
            String[] headers = {"Username", "Full Name", "Email"};
            List<String[]> rows = new ArrayList<>();
            for (User u : results) {
                rows.add(new String[]{u.username(), u.fullName(), u.email()});
            }
            System.out.println(FormatUtils.formatTable(headers, rows));
        }
    }
    
    // ========== Команды ролей ==========
    
    public static void createRole(Scanner scanner, RBACSystem system) {
        String name = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
        String description = ConsoleUtils.promptString(scanner, "Введите описание: ", true);
        
        try {
            Role role = new Role(name, description);
            system.getRoleManager().add(role);
            System.out.println("Роль создана: " + role.getName());
            system.getAuditLog().log(
                    "ROLE_CREATE",
                    system.getCurrentUser(),
                    name,
                    "Создана роль"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void deleteRole(Scanner scanner, RBACSystem system) {
        String roleName = ConsoleUtils.promptString(scanner, "Введите название роли для удаления: ", true);
        
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
                system.getAuditLog().log(
                        "ROLE_DELETE",
                        system.getCurrentUser(),
                        roleName,
                        "Роль удалена"
                );
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
            System.out.println(FormatUtils.formatHeader("Список ролей"));
            String[] headers = {"Name", "Description"};
            List<String[]> rows = new ArrayList<>();
            for (Role r : roles) {
                rows.add(new String[]{r.getName(), r.getDescription()});
            }
            System.out.println(FormatUtils.formatTable(headers, rows));
        }
    }
    
    public static void addPermissionToRole(Scanner scanner, RBACSystem system) {
        String roleName = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
        String permName = ConsoleUtils.promptString(scanner, "Введите название права (например, READ): ", true);
        String resource = ConsoleUtils.promptString(scanner, "Введите ресурс (например, users): ", true);
        String description = ConsoleUtils.promptString(scanner, "Введите описание: ", true);
        
        try {
            Permission permission = new Permission(permName, resource, description);
            system.getRoleManager().addPermissionToRole(roleName, permission);
            System.out.println("Право добавлено к роли");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void removePermissionFromRole(Scanner scanner, RBACSystem system) {
        String roleName = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
        String permName = ConsoleUtils.promptString(scanner, "Введите название права: ", true);
        String resource = ConsoleUtils.promptString(scanner, "Введите ресурс: ", true);
        
        try {
            Permission permission = new Permission(permName, resource, "temp");
            system.getRoleManager().removePermissionFromRole(roleName, permission);
            System.out.println("Право удалено из роли");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void searchRoles(Scanner scanner, RBACSystem system) {
        System.out.println(FormatUtils.formatHeader("Поиск ролей"));
        System.out.println("1. По названию");
        System.out.println("2. По названию (содержит)");
        System.out.println("3. По праву");
        System.out.println("4. Минимум N прав");
        String choice = ConsoleUtils.promptString(scanner, "Ваш выбор: ", true);
        
        RoleFilter filter = null;
        switch (choice) {
            case "1":
                filter = RoleFilters.byName(
                        ConsoleUtils.promptString(scanner, "Введите название: ", true));
                break;
            case "2":
                filter = RoleFilters.byNameContains(
                        ConsoleUtils.promptString(scanner, "Введите подстроку: ", true));
                break;
            case "3":
                String permName = ConsoleUtils.promptString(scanner, "Введите название права: ", true);
                String resource = ConsoleUtils.promptString(scanner, "Введите ресурс: ", true);
                filter = RoleFilters.hasPermission(permName, resource);
                break;
            case "4":
                try {
                    int n = ConsoleUtils.promptInt(scanner, "Введите минимальное количество прав: ", 0, Integer.MAX_VALUE);
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
            String[] headers = {"Name", "Description"};
            List<String[]> rows = new ArrayList<>();
            for (Role r : results) {
                rows.add(new String[]{r.getName(), r.getDescription()});
            }
            System.out.println(FormatUtils.formatTable(headers, rows));
        }
    }
    
    // ========== Команды назначений ==========
    
    public static void createAssignment(Scanner scanner, RBACSystem system) {
        String username = ConsoleUtils.promptString(scanner, "Введите username: ", true);
        String roleName = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
        String type = ConsoleUtils.promptString(scanner, "Введите тип (PERMANENT/TEMPORARY): ", true).toUpperCase();
        String reason = ConsoleUtils.promptString(scanner, "Введите причину назначения: ", true);
        
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
            String dateStr = ConsoleUtils.promptString(scanner, "Введите дату истечения (YYYY-MM-DD): ", true);
            try {
                if (!ValidationUtils.isValidDate(dateStr)) {
                    System.out.println("Неверный формат даты");
                    return;
                }
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
            system.getAuditLog().log(
                    "ASSIGNMENT_CREATE",
                    system.getCurrentUser(),
                    assignment.assignmentId(),
                    assignment.summary()
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void revokeAssignment(Scanner scanner, RBACSystem system) {
        String input = ConsoleUtils.promptString(scanner, "Введите assignment ID или username: ", true);
        
        RoleAssignment assignment = null;
        if (input.startsWith("assignment_")) {
            assignment = system.getAssignmentManager().findById(input).orElse(null);
        } else {
            String roleName = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
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
                system.getAuditLog().log(
                        "ASSIGNMENT_REVOKE",
                        system.getCurrentUser(),
                        assignment.assignmentId(),
                        "Назначение отозвано"
                );
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
            System.out.println(FormatUtils.formatHeader("Список назначений"));
            assignments.forEach(a -> System.out.println(FormatUtils.formatBox(a.summary())));
        }
    }
    
    public static void listActiveAssignments(Scanner scanner, RBACSystem system) {
        List<RoleAssignment> assignments = system.getAssignmentManager().getActiveAssignments();
        if (assignments.isEmpty()) {
            System.out.println("Активных назначений нет");
        } else {
            System.out.println(FormatUtils.formatHeader("Активные назначения"));
            assignments.forEach(a -> System.out.println(FormatUtils.formatBox(a.summary())));
        }
    }
    
    public static void listExpiredAssignments(Scanner scanner, RBACSystem system) {
        List<RoleAssignment> assignments = system.getAssignmentManager().getExpiredAssignments();
        if (assignments.isEmpty()) {
            System.out.println("Истёкших назначений нет");
        } else {
            System.out.println(FormatUtils.formatHeader("Истёкшие назначения"));
            assignments.forEach(a -> System.out.println(FormatUtils.formatBox(a.summary())));
        }
    }
    
    public static void extendAssignment(Scanner scanner, RBACSystem system) {
        String assignmentId = ConsoleUtils.promptString(scanner, "Введите assignment ID: ", true);
        String dateStr = ConsoleUtils.promptString(scanner, "Введите новую дату истечения (YYYY-MM-DD): ", true);
        
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
        System.out.println(FormatUtils.formatHeader("Поиск назначений"));
        System.out.println("1. По пользователю");
        System.out.println("2. По роли");
        System.out.println("3. По типу (PERMANENT/TEMPORARY)");
        System.out.println("4. По статусу (активное/неактивное)");
        System.out.println("5. Назначённые после даты");
        System.out.println("6. Истекающие до даты");
        String choice = ConsoleUtils.promptString(scanner, "Ваш выбор: ", true);
        
        AssignmentFilter filter = null;
        switch (choice) {
            case "1":
                String username = ConsoleUtils.promptString(scanner, "Введите username: ", true);
                Optional<User> userOpt = system.getUserManager().findByUsername(username);
                if (userOpt.isPresent()) {
                    filter = AssignmentFilters.byUser(userOpt.get());
                } else {
                    System.out.println("Пользователь не найден");
                    return;
                }
                break;
            case "2":
                String roleName = ConsoleUtils.promptString(scanner, "Введите название роли: ", true);
                Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                if (roleOpt.isPresent()) {
                    filter = AssignmentFilters.byRole(roleOpt.get());
                } else {
                    System.out.println("Роль не найдена");
                    return;
                }
                break;
            case "3":
                filter = AssignmentFilters.byType(
                        ConsoleUtils.promptString(scanner, "Введите тип (PERMANENT/TEMPORARY): ", true).toUpperCase());
                break;
            case "4":
                boolean active = ConsoleUtils.promptYesNo(scanner, "Активные? (y/n): ");
                filter = active ? AssignmentFilters.activeOnly() : AssignmentFilters.inactiveOnly();
                break;
            case "5":
                String dateAfter = ConsoleUtils.promptString(scanner, "Введите дату (YYYY-MM-DDTHH:mm:ss): ", true);
                filter = AssignmentFilters.assignedAfter(dateAfter);
                break;
            case "6":
                String dateBefore = ConsoleUtils.promptString(scanner, "Введите дату (YYYY-MM-DDTHH:mm:ss): ", true);
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
            results.forEach(a -> System.out.println(FormatUtils.formatBox(a.summary())));
        }
    }
    
    // ========== Команды просмотра прав ==========
    
    public static void showUserPermissions(Scanner scanner, RBACSystem system) {
        String username = ConsoleUtils.promptString(scanner, "Введите username: ", true);
        
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
        String username = ConsoleUtils.promptString(scanner, "Введите username: ", true);
        String permName = ConsoleUtils.promptString(scanner, "Введите название права: ", true);
        String resource = ConsoleUtils.promptString(scanner, "Введите ресурс: ", true);
        
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
        if (ConsoleUtils.promptYesNo(scanner, "Сохранить данные перед выходом? (y/n): ")) {
            save(scanner, system);
        }
        System.out.println("Выход из программы");
        System.exit(0);
    }
    
    public static void save(Scanner scanner, RBACSystem system) {
        String filename = ConsoleUtils.promptString(scanner, "Введите имя файла (по умолчанию data.txt): ", false);
        if (filename.isEmpty()) {
            filename = "data.txt";
        }
        
        // Простая реализация сохранения (можно улучшить)
        System.out.println("Сохранение данных в " + filename + "...");
        System.out.println("(Функция сохранения требует дополнительной реализации)");
    }
    
    public static void load(Scanner scanner, RBACSystem system) {
        String filename = ConsoleUtils.promptString(scanner, "Введите имя файла: ", true);
        
        // Простая реализация загрузки (можно улучшить)
        System.out.println("Загрузка данных из " + filename + "...");
        System.out.println("(Функция загрузки требует дополнительной реализации)");
    }

    public static void saveAsync(Scanner scanner, RBACSystem system) {
        String filename = ConsoleUtils.promptString(scanner, "Введите имя файла (по умолчанию data.txt): ", false);
        if (filename.isEmpty()) {
            filename = "data.txt";
        }
        final String fName = filename;
        System.out.println("Сохранение данных запущено в фоновом режиме...");
        system.getExecutorService().submit(() -> {
            try {
                Thread.sleep(1500); // Имитация долгого сохранения
                System.out.println("\n[Асинхронно] Сохранение завершено в " + fName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // ========== Отчёты и аудит ==========

    public static void showAuditLog(Scanner scanner, RBACSystem system) {
        system.getAuditLog().printLog();
    }

    public static void reportUsers(Scanner scanner, RBACSystem system) {
        String report = ReportGenerator.generateUserReport(system.getUserManager(), system.getAssignmentManager());
        System.out.println(FormatUtils.formatHeader("Отчёт по пользователям"));
        System.out.println(report);
    }

    public static void reportRoles(Scanner scanner, RBACSystem system) {
        String report = ReportGenerator.generateRoleReport(system.getRoleManager(), system.getAssignmentManager());
        System.out.println(FormatUtils.formatHeader("Отчёт по ролям"));
        System.out.println(report);
    }

    public static void reportUsersAsync(Scanner scanner, RBACSystem system) {
        System.out.println("Генерация отчёта запущена в фоновом режиме...");
        system.getExecutorService().submit(() -> {
            try {
                String report = ReportGenerator.generateUserReportParallel(system.getUserManager(), system.getAssignmentManager());
                System.out.println("\n" + FormatUtils.formatHeader("Асинхронный Отчёт по пользователям"));
                System.out.println(report);
            } catch (Exception e) {
                System.out.println("Ошибка при генерации отчёта: " + e.getMessage());
            }
        });
    }

    public static void reportMatrix(Scanner scanner, RBACSystem system) {
        String report = ReportGenerator.generatePermissionMatrix(system.getUserManager(), system.getAssignmentManager());
        System.out.println(FormatUtils.formatHeader("Матрица прав"));
        System.out.println(report);
    }
}


