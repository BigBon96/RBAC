public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        String trimmed = username.trim();
        if (trimmed.length() < 3 || trimmed.length() > 20) {
            return false;
        }
        return trimmed.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String trimmed = email.trim();
        return trimmed.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        String trimmed = date.trim();
        // Базовая проверка формата YYYY-MM-DD
        return trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    public static String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        // Сжимаем последовательности пробелов в один
        return trimmed.replaceAll("\\s+", " ");
    }

    public static void requireNonEmpty(String value, String fieldName) {
        String normalized = normalizeString(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName == null || fieldName.isBlank()
                            ? "Значение не должно быть пустым"
                            : "Поле '" + fieldName + "' не должно быть пустым"
            );
        }
    }
}

