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
}
