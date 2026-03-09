import java.util.List;
import java.util.Scanner;

public final class ConsoleUtils {

    private ConsoleUtils() {
    }

    public static String promptString(Scanner scanner, String message, boolean required) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine();
            value = ValidationUtils.normalizeString(value);
            if (!required || !value.isEmpty()) {
                return value;
            }
            System.out.println("Значение обязательно для ввода. Попробуйте ещё раз.");
        }
    }

    public static int promptInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();
            try {
                int result = Integer.parseInt(value);
                if (result < min || result > max) {
                    System.out.printf("Введите число в диапазоне [%d, %d].%n", min, max);
                } else {
                    return result;
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректное число. Попробуйте ещё раз.");
            }
        }
    }

    public static boolean promptYesNo(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim().toLowerCase();
            if (value.startsWith("y") || value.startsWith("д")) {
                return true;
            }
            if (value.startsWith("n") || value.startsWith("н")) {
                return false;
            }
            System.out.println("Введите yes/no или y/n.");
        }
    }

    public static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Список вариантов пуст");
        }
        while (true) {
            System.out.println(message);
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  %d) %s%n", i + 1, options.get(i));
            }
            int choice = promptInt(scanner, "Ваш выбор: ", 1, options.size());
            return options.get(choice - 1);
        }
    }
}

