import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RBACSystem system = new RBACSystem();
        system.initialize();
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== RBAC System ===");
        System.out.println("Введите 'help' для справки по командам");
        System.out.println("Введите 'exit' для выхода\n");
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            Command command = CommandParser.parse(input);
            if (command != null) {
                command.execute(scanner, system);
            } else {
                System.out.println("Неизвестная команда. Введите 'help' для справки.");
            }
            
            System.out.println();
        }
    }
}
