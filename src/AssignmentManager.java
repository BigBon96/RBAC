import java.util.*;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment> {
    private final Map<String, RoleAssignment> assignments = new HashMap<>();
    private final UserManager userManager;
    private final RoleManager roleManager;

    public AssignmentManager(UserManager userManager, RoleManager roleManager) {
        this.userManager = userManager;
        this.roleManager = roleManager;
    }

    @Override
    public void add(RoleAssignment item) {
        if (item == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        if (assignments.containsKey(item.assignmentId())) {
            throw new IllegalArgumentException("Assignment with id '" + item.assignmentId() + "' already exists");
        }

        // Проверка существования пользователя и роли
        if (!userManager.exists(item.user().username())) {
            throw new IllegalArgumentException("User '" + item.user().username() + "' does not exist");
        }
        if (!roleManager.exists(item.role().getName())) {
            throw new IllegalArgumentException("Role '" + item.role().getName() + "' does not exist");
        }

        // Проверка на дублирование: одна роль не может быть назначена пользователю дважды одновременно
        if (userHasRole(item.user(), item.role())) {
            throw new IllegalArgumentException("User '" + item.user().username() + 
                    "' already has active assignment for role '" + item.role().getName() + "'");
        }

        assignments.put(item.assignmentId(), item);
    }

    @Override
    public boolean remove(RoleAssignment item) {
        if (item == null) {
            return false;
        }
        return assignments.remove(item.assignmentId()) != null;
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        return Optional.ofNullable(assignments.get(id));
    }

    @Override
    public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public int count() {
        return assignments.size();
    }

    @Override
    public void clear() {
        assignments.clear();
    }

    public List<RoleAssignment> findByUser(User user) {
        return assignments.values().stream()
                .filter(assignment -> assignment.user().equals(user))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByRole(Role role) {
        return assignments.values().stream()
                .filter(assignment -> assignment.role().equals(role))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        return assignments.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        return assignments.values().stream()
                .filter(filter::test)
                .sorted(sorter)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> getActiveAssignments() {
        return assignments.values().stream()
                .filter(RoleAssignment::isActive)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> getExpiredAssignments() {
        return assignments.values().stream()
                .filter(assignment -> !assignment.isActive())
                .collect(Collectors.toList());
    }

    public boolean userHasRole(User user, Role role) {
        return assignments.values().stream()
                .anyMatch(assignment -> assignment.user().equals(user) 
                        && assignment.role().equals(role) 
                        && assignment.isActive());
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        return getUserPermissions(user).stream()
                .anyMatch(perm -> perm.matches(permissionName, resource));
    }

    public Set<Permission> getUserPermissions(User user) {
        return assignments.values().stream()
                .filter(assignment -> assignment.user().equals(user) && assignment.isActive())
                .flatMap(assignment -> assignment.role().getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public void revokeAssignment(String assignmentId) {
        RoleAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment with id '" + assignmentId + "' not found");
        }
        if (assignment instanceof PermanentAssignment permAssignment) {
            permAssignment.revoke();
        } else if (assignment instanceof TemporaryAssignment) {
            // Для временных назначений можно удалить или оставить как истекшее
            // Удаляем из активных
            assignments.remove(assignmentId);
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        RoleAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment with id '" + assignmentId + "' not found");
        }
        if (!(assignment instanceof TemporaryAssignment)) {
            throw new IllegalArgumentException("Assignment is not temporary");
        }
        TemporaryAssignment tempAssignment = (TemporaryAssignment) assignment;
        tempAssignment.extend(newExpirationDate);
    }
}
