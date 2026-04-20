import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RBACSystem system = new RBACSystem();
        system.initialize();
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== RBAC System ===");
        System.out.println("Система инициализирована. Запущено фоновое логирование и периодические задачи.");
        System.out.println("Текущая статистика:");
        System.out.println(system.generateStatistics());
        
        System.out.println("Введите 'help' для справки по командам");
        System.out.println("Введите 'exit' для выхода\n");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nОстановка системы. Завершение фоновых задач...");
            system.getScheduledExecutorService().shutdownNow();
            system.getExecutorService().shutdownNow();
            System.out.println("Система успешно остановлена.");
        }));
        
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
