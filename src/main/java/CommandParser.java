import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        // Команды пользователей
        commands.put("user-create", Commands::createUser);
        commands.put("user-update", Commands::updateUser);
        commands.put("user-delete", Commands::deleteUser);
        commands.put("user-list", Commands::listUsers);
        commands.put("user-search", Commands::searchUsers);

        // Команды ролей
        commands.put("role-create", Commands::createRole);
        commands.put("role-delete", Commands::deleteRole);
        commands.put("role-list", Commands::listRoles);
        commands.put("role-permission-add", Commands::addPermissionToRole);
        commands.put("role-permission-remove", Commands::removePermissionFromRole);
        commands.put("role-search", Commands::searchRoles);

        // Команды назначений
        commands.put("assignment-create", Commands::createAssignment);
        commands.put("assignment-revoke", Commands::revokeAssignment);
        commands.put("assignment-list", Commands::listAssignments);
        commands.put("assignment-active", Commands::listActiveAssignments);
        commands.put("assignment-expired", Commands::listExpiredAssignments);
        commands.put("assignment-extend", Commands::extendAssignment);
        commands.put("assignment-search", Commands::searchAssignments);

        // Команды просмотра прав
        commands.put("permissions-user", Commands::showUserPermissions);
        commands.put("permissions-check", Commands::checkPermission);

        // Служебные команды
        commands.put("help", Commands::showHelp);
        commands.put("stats", Commands::showStats);
        commands.put("clear", Commands::clearScreen);
        commands.put("exit", Commands::exit);
        commands.put("save", Commands::save);
        commands.put("load", Commands::load);
    }

    public static Command parse(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        return commands.get(commandName);
    }

    public static void printHelp() {
        System.out.println("=== Справка по командам ===\n");
        
        System.out.println("Команды пользователей:");
        System.out.println("  user-create          - создать пользователя");
        System.out.println("  user-update          - обновить пользователя");
        System.out.println("  user-delete          - удалить пользователя");
        System.out.println("  user-list            - список всех пользователей");
        System.out.println("  user-search          - поиск пользователей\n");

        System.out.println("Команды ролей:");
        System.out.println("  role-create          - создать роль");
        System.out.println("  role-delete          - удалить роль");
        System.out.println("  role-list            - список всех ролей");
        System.out.println("  role-permission-add  - добавить право к роли");
        System.out.println("  role-permission-remove - удалить право из роли");
        System.out.println("  role-search          - поиск ролей\n");

        System.out.println("Команды назначений:");
        System.out.println("  assignment-create    - создать назначение");
        System.out.println("  assignment-revoke    - отозвать назначение");
        System.out.println("  assignment-list      - список всех назначений");
        System.out.println("  assignment-active    - только активные назначения");
        System.out.println("  assignment-expired   - истёкшие назначения");
        System.out.println("  assignment-extend    - продлить временное назначение");
        System.out.println("  assignment-search   - поиск назначений\n");

        System.out.println("Команды просмотра прав:");
        System.out.println("  permissions-user     - все права пользователя");
        System.out.println("  permissions-check    - проверить право пользователя\n");

        System.out.println("Служебные команды:");
        System.out.println("  help                 - показать эту справку");
        System.out.println("  stats                - статистика системы");
        System.out.println("  clear                - очистить экран");
        System.out.println("  exit                 - выход из программы");
        System.out.println("  save                 - сохранить данные в файл");
        System.out.println("  load                 - загрузить данные из файла");
    }
}

