import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class LoadTest {

    @Test
    public void testConcurrentLoad() throws InterruptedException {
        RBACSystem system = new RBACSystem();
        system.initialize();
        
        int numThreads = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String suffix = threadId + "_" + j;
                    try {
                        // Создаем пользователя
                        String username = "user_" + suffix;
                        User u = User.create(username, "Full Name " + suffix, "email" + suffix + "@test.com");
                        system.getUserManager().add(u);
                        
                        // Обновляем пользователя
                        system.getUserManager().update(username, "Updated " + suffix, "newemail" + suffix + "@test.com");
                        
                        // Создаем роль
                        String roleName = "Role_" + suffix;
                        Role r = new Role(roleName, "Desc");
                        system.getRoleManager().add(r);
                        
                        // Добавляем право
                        system.getRoleManager().addPermissionToRole(roleName, new Permission("READ", "data_" + suffix, ""));
                        
                        // Назначаем роль
                        AssignmentMetadata metadata = AssignmentMetadata.now("system", "Test");
                        RoleAssignment assignment = new PermanentAssignment(u, r, metadata);
                        system.getAssignmentManager().add(assignment);
                        
                        // Фильтруем/ищем
                        system.getUserManager().findByFilterParallel(UserFilters.byUsernameContains(threadId + "_"));
                        system.getRoleManager().findByFilterParallel(RoleFilters.byNameContains("Role_"));
                        system.getAssignmentManager().findByFilterParallel(AssignmentFilters.byUser(u));
                        
                        // Пишем в лог
                        system.getAuditLog().log("TEST_ACTION", "system", username, "Did some test work");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        executor.shutdown();
        boolean finished = executor.awaitTermination(30, TimeUnit.SECONDS);
        assertTrue(finished, "Load test timeout");
        
        // В инициализации создается 1 пользователь, 3 роли и 1 назначение
        int expectedUsers = 1 + numThreads * operationsPerThread;
        assertEquals(expectedUsers, system.getUserManager().count(), "User count mismatch");
        
        int expectedRoles = 3 + numThreads * operationsPerThread;
        assertEquals(expectedRoles, system.getRoleManager().count(), "Role count mismatch");
        
        int expectedAssignments = 1 + numThreads * operationsPerThread;
        assertEquals(expectedAssignments, system.getAssignmentManager().count(), "Assignment count mismatch");
        
        // Даем потоку логов немного времени на обработку очереди
        Thread.sleep(500);
        
        // Проверяем логи (в initialize есть 1 лог + numThreads * operationsPerThread)
        // Но в initialize() у нас нет логов, потому что там напрямую создается assignment? 
        // В RBACSystem.initialize() не вызывается auditLog.log, так что должно быть:
        // numThreads * operationsPerThread логов
        assertEquals(numThreads * operationsPerThread, system.getAuditLog().getAll().size(), "AuditLog count mismatch");
    }
}
