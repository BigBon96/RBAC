import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class PeriodicTaskTest {

    @Test
    public void testExpirationPeriodicTask() throws InterruptedException {
        RBACSystem system = new RBACSystem();
        system.initialize();
        
        // Добавляем тестового пользователя и роль
        User testUser = User.create("exp_user", "Exp User", "exp@mail.com");
        system.getUserManager().add(testUser);
        Role testRole = new Role("ExpRole", "Desc");
        system.getRoleManager().add(testRole);
        
        // Создаем временное назначение, которое УЖЕ истекло
        AssignmentMetadata metadata = AssignmentMetadata.now("system", "Test Expiration");
        TemporaryAssignment tempAssignment = new TemporaryAssignment(testUser, testRole, metadata);
        tempAssignment.extend(LocalDateTime.now().minusDays(1).toString()); // Истекло вчера
        system.getAssignmentManager().add(tempAssignment);
        
        assertTrue(tempAssignment.isExpired(), "Assignment should be expired immediately");
        assertNotNull(system.getAssignmentManager().findById(tempAssignment.assignmentId()).orElse(null));
        
        // Ждем 12 секунд, чтобы периодическая задача отработала (она запускается раз в 10 сек)
        Thread.sleep(12000);
        
        // Проверяем, что истекшее назначение удалено из активных / отозвано
        assertNull(system.getAssignmentManager().findById(tempAssignment.assignmentId()).orElse(null));
        
        // Очищаем
        system.getScheduledExecutorService().shutdownNow();
        system.getExecutorService().shutdownNow();
    }
}
