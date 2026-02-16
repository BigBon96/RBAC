public record User(String username, String fullName, String email) {

    public static User validate(String username, String fullName, String email) {
        if (username == null || fullName == null || email == null) {
            throw new IllegalArgumentException("Поля пользователя не должны быть null");
        }

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Поля пользователя не должны быть пустыми");
        }

        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("Длина username должна быть от 3 до 20 символов");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("username может содержать только латинские буквы, цифры и подчёркивание");
        }

        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new IllegalArgumentException("Некорректный формат email");
        }

        return new User(username, fullName, email);
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}


