import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RBACSystem {
    private final UserManager userManager;
    private final RoleManager roleManager;
    private final AssignmentManager assignmentManager;
    private final AuditLog auditLog;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private String currentUser;

    public RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager(userManager, roleManager);
        this.auditLog = new AuditLog();
        this.executorService = Executors.newFixedThreadPool(4);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.currentUser = "system";
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void initialize() {
        // Создание предустановленных прав доступа
        Permission readUsers = new Permission("READ", "users", "Read users");
        Permission writeUsers = new Permission("WRITE", "users", "Write users");
        Permission deleteUsers = new Permission("DELETE", "users", "Delete users");
        Permission readReports = new Permission("READ", "reports", "Read reports");
        Permission writeReports = new Permission("WRITE", "reports", "Write reports");

        // Создание ролей по умолчанию
        Role adminRole = new Role("Administrator", "Full system access");
        adminRole.addPermission(readUsers);
        adminRole.addPermission(writeUsers);
        adminRole.addPermission(deleteUsers);
        adminRole.addPermission(readReports);
        adminRole.addPermission(writeReports);
        roleManager.add(adminRole);

        Role managerRole = new Role("Manager", "Management access");
        managerRole.addPermission(readUsers);
        managerRole.addPermission(writeUsers);
        managerRole.addPermission(readReports);
        roleManager.add(managerRole);

        Role userRole = new Role("User", "Basic user access");
        userRole.addPermission(readUsers);
        userRole.addPermission(readReports);
        roleManager.add(userRole);

        // Создание админ-пользователя
        User admin = User.create("admin", "System Administrator", "admin@system.com");
        userManager.add(admin);

        // Назначение роли администратора
        AssignmentMetadata metadata = AssignmentMetadata.now("system", "Initial setup");
        PermanentAssignment adminAssignment = new PermanentAssignment(admin, adminRole, metadata);
        assignmentManager.add(adminAssignment);

        this.currentUser = "admin";

        // Запуск периодической задачи
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                // Находим истёкшие назначения (минимальная блокировка, так как ConcurrentHashMap)
                List<RoleAssignment> expired = assignmentManager.getExpiredAssignments();
                if (!expired.isEmpty()) {
                    for (RoleAssignment a : expired) {
                        assignmentManager.revokeAssignment(a.assignmentId());
                        auditLog.log("ASSIGNMENT_EXPIRED", "system", a.assignmentId(), "Временное назначение истекло и было отозвано");
                    }
                }
                
                // Пишем статистику в лог
                String stats = generateStatistics();
                auditLog.log("SYSTEM_STATS", "system", "statistics", stats);
            } catch (Exception e) {
                System.err.println("Ошибка в фоновой задаче: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public String generateStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Статистика системы ===\n\n");
        
        int userCount = userManager.count();
        int roleCount = roleManager.count();
        int assignmentCount = assignmentManager.count();
        int activeAssignments = assignmentManager.getActiveAssignments().size();
        int expiredAssignments = assignmentManager.getExpiredAssignments().size();
        
        stats.append(String.format("Пользователей: %d\n", userCount));
        stats.append(String.format("Ролей: %d\n", roleCount));
        stats.append(String.format("Назначений: %d (активных: %d, истёкших: %d)\n", 
            assignmentCount, activeAssignments, expiredAssignments));
        
        if (userCount > 0) {
            double avgRolesPerUser = (double) assignmentCount / userCount;
            stats.append(String.format("Среднее количество ролей на пользователя: %.2f\n", avgRolesPerUser));
        }
        
        // Топ-3 самых популярных ролей
        List<Role> topRoles = assignmentManager.findAll()
            .stream()
            .collect(Collectors.groupingBy(RoleAssignment::role, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .limit(3)
            .map(e -> e.getKey())
            .collect(Collectors.toList());
        
        if (!topRoles.isEmpty()) {
            stats.append("\nТоп-3 самых популярных ролей:\n");
            for (int i = 0; i < topRoles.size(); i++) {
                Role role = topRoles.get(i);
                long count = assignmentManager.findAll()
                    .stream()
                    .filter(a -> a.role().equals(role))
                    .count();
                stats.append(String.format("  %d. %s (%d назначений)\n", i + 1, role.getName(), count));
            }
        }
        
        return stats.toString();
    }
}


