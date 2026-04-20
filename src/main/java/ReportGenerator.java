import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class ReportGenerator {

    private ReportGenerator() {
    }

    public static String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        List<User> users = new ArrayList<>(userManager.findAll());
        users.sort(Comparator.comparing(User::username));

        List<String[]> rows = new ArrayList<>();
        for (User user : users) {
            List<RoleAssignment> assignments = assignmentManager.findByUser(user);
            String roles = assignments.stream()
                    .map(a -> a.role().getName())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            rows.add(new String[]{
                    user.username(),
                    user.fullName(),
                    user.email(),
                    roles.isEmpty() ? "-" : roles
            });
        }

        String[] headers = {"Username", "Full Name", "Email", "Roles"};
        return FormatUtils.formatTable(headers, rows);
    }

    public static String generateUserReportParallel(UserManager userManager, AssignmentManager assignmentManager) {
        List<User> users = userManager.findAll();

        List<String[]> rows = users.parallelStream()
                .sorted(Comparator.comparing(User::username))
                .map(user -> {
                    List<RoleAssignment> assignments = assignmentManager.findByUser(user);
                    String roles = assignments.stream()
                            .map(a -> a.role().getName())
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(", "));
                    return new String[]{
                            user.username(),
                            user.fullName(),
                            user.email(),
                            roles.isEmpty() ? "-" : roles
                    };
                })
                .collect(Collectors.toList());

        String[] headers = {"Username", "Full Name", "Email", "Roles"};
        return FormatUtils.formatTable(headers, rows);
    }

    public static String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        List<Role> roles = new ArrayList<>(roleManager.findAll());
        roles.sort(Comparator.comparing(Role::getName));

        List<String[]> rows = new ArrayList<>();
        for (Role role : roles) {
            long usersCount = assignmentManager.findByRole(role).stream()
                    .map(RoleAssignment::user)
                    .distinct()
                    .count();
            rows.add(new String[]{
                    role.getName(),
                    role.getDescription(),
                    String.valueOf(usersCount)
            });
        }

        String[] headers = {"Role", "Description", "Users"};
        return FormatUtils.formatTable(headers, rows);
    }

    public static String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {
        List<User> users = new ArrayList<>(userManager.findAll());
        users.sort(Comparator.comparing(User::username));

        Set<String> resources = new TreeSet<>();
        for (User user : users) {
            Set<Permission> perms = assignmentManager.getUserPermissions(user);
            for (Permission p : perms) {
                resources.add(p.resource());
            }
        }

        List<String> headersList = new ArrayList<>();
        headersList.add("Username");
        headersList.addAll(resources);
        String[] headers = headersList.toArray(new String[0]);

        List<String[]> rows = new ArrayList<>();
        for (User user : users) {
            Set<Permission> perms = assignmentManager.getUserPermissions(user);
            String[] row = new String[headers.length];
            row[0] = user.username();
            int idx = 1;
            for (String resource : resources) {
                boolean hasAny = perms.stream().anyMatch(p -> p.resource().equals(resource));
                row[idx++] = hasAny ? "X" : "";
            }
            rows.add(row);
        }

        return FormatUtils.formatTable(headers, rows);
    }

    public static String generatePermissionMatrixParallel(UserManager userManager, AssignmentManager assignmentManager) {
        List<User> users = userManager.findAll();

        List<String> sortedResources = users.parallelStream()
                .flatMap(user -> assignmentManager.getUserPermissions(user).stream())
                .map(Permission::resource)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> headersList = new ArrayList<>();
        headersList.add("Username");
        headersList.addAll(sortedResources);
        String[] headers = headersList.toArray(new String[0]);

        List<String[]> rows = users.parallelStream()
                .sorted(Comparator.comparing(User::username))
                .map(user -> {
                    Set<Permission> perms = assignmentManager.getUserPermissions(user);
                    String[] row = new String[headers.length];
                    row[0] = user.username();
                    int idx = 1;
                    for (String resource : sortedResources) {
                        boolean hasAny = perms.stream().anyMatch(p -> p.resource().equals(resource));
                        row[idx++] = hasAny ? "X" : "";
                    }
                    return row;
                })
                .collect(Collectors.toList());

        return FormatUtils.formatTable(headers, rows);
    }

    public static void exportToFile(String report, String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Имя файла отчёта не должно быть пустым");
        }
        try {
            Files.writeString(Path.of(filename), report);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить отчёт: " + e.getMessage(), e);
        }
    }
}

