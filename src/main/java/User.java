public record User(String username, String fullName, String email) {
    public static User create(String username, String fullName, String email) {
        if (username == null || fullName == null || email == null) {
            throw new IllegalArgumentException("Поля пользователя не должны быть null!");
        }

        ValidationUtils.requireNonEmpty(username, "username");
        ValidationUtils.requireNonEmpty(fullName, "fullName");
        ValidationUtils.requireNonEmpty(email, "email");

        if (!ValidationUtils.isValidUsername(username)) {
            throw new IllegalArgumentException("Недопустимые символы или длина в имени пользователя!");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Некорректный формат email!");
        }

        return new User(
                ValidationUtils.normalizeString(username),
                ValidationUtils.normalizeString(fullName),
                ValidationUtils.normalizeString(email)
        );
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}

