void main() {
    // Валидный пользователь
    User validUser = User.validate("username_1", "Valid User", "user@example.com");
    IO.println(validUser.format());

    // Слишком короткий username
    try {
        User.validate("ab", "Short Username", "short@example.com");
    } catch (IllegalArgumentException e) {
        IO.println(e.getMessage());
    }

    // Недопустимые символы в username
    try {
        User.validate("user-name", "Invalid Chars", "user2@example.com");
    } catch (IllegalArgumentException e) {
        IO.println(e.getMessage());
    }

    // Некорректный email
    try {
        User.validate("user_ok", "Bad Email", "bad-email");
    } catch (IllegalArgumentException e) {
        IO.println(e.getMessage());
    }

    // Тесты для Permission
    Permission p1 = new Permission("read", "Users", "Can read users");
    IO.println(p1.format());

    Permission p2 = new Permission("delete reports", "Reports", "Can delete reports");
    IO.println(p2.format());

    IO.println("matches READ/users: " + p1.matches("READ", "users"));
    IO.println("matches DEL/reports: " + p2.matches("DEL", "reports"));
    IO.println("matches WRITE/users: " + p1.matches("WRITE", "users"));
}
