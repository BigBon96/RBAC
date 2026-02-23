import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Валидный пользователь
        User validUser = User.create("username_1", "Valid User", "user@example.com");
        System.out.println(validUser.format());

        // Слишком короткий username
        try {
            User.create("ab", "Short Username", "short@example.com");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Недопустимые символы в username
        try {
            User.create("user-name", "Invalid Chars", "user2@example.com");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Некорректный email
        try {
            User.create("user_ok", "Bad Email", "bad-email");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Тесты для Permission
        Permission p1 = new Permission("read", "Users", "Can read users");
        System.out.println(p1.format());

        Permission p2 = new Permission("delete reports", "Reports", "Can delete reports");
        System.out.println(p2.format());

        System.out.println("matches READ/users: " + p1.matches("READ", "users"));
        System.out.println("matches DEL/reports: " + p2.matches("DEL", "reports"));
        System.out.println("matches WRITE/users: " + p1.matches("WRITE", "users"));

        // Тесты для Role
        Role admin = new Role("Administrator", "Full system access");
        admin.addPermission(p1);
        admin.addPermission(p2);

        System.out.println(admin.format());
        System.out.println("admin has READ/users: " + admin.hasPermission("READ", "users"));
        System.out.println("admin has WRITE/users: " + admin.hasPermission("WRITE", "users"));

        // Тесты для AssignmentMetadata и назначений
        AssignmentMetadata meta = AssignmentMetadata.now(validUser.username(), "Initial setup");
        System.out.println(meta.format());

        TemporaryAssignment ta = new TemporaryAssignment(validUser, admin, meta);
        ta.extend(LocalDateTime.now().plusDays(10).toString());
        System.out.println(ta.summary());

        PermanentAssignment pa = new PermanentAssignment(validUser, admin, meta);
        System.out.println(pa.summary());
        pa.revoke();
        System.out.println(pa.summary());
    }
}
