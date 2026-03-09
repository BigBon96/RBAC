import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void logAndGetAll_storeEntries() {
        AuditLog log = new AuditLog();
        log.log("ACTION", "user1", "target1", "details1");
        log.log("ACTION2", "user2", "target2", "details2");

        List<AuditLog.AuditEntry> all = log.getAll();
        assertEquals(2, all.size());
        assertEquals("user1", all.get(0).performer());
    }

    @Test
    void getByPerformer_filtersCorrectly() {
        AuditLog log = new AuditLog();
        log.log("ACTION", "user1", "t1", "d1");
        log.log("ACTION", "user2", "t2", "d2");

        List<AuditLog.AuditEntry> user1Entries = log.getByPerformer("user1");
        assertEquals(1, user1Entries.size());
        assertEquals("user1", user1Entries.get(0).performer());
    }

    @Test
    void getByAction_filtersCorrectly() {
        AuditLog log = new AuditLog();
        log.log("CREATE", "user1", "t1", "d1");
        log.log("DELETE", "user1", "t2", "d2");

        List<AuditLog.AuditEntry> createEntries = log.getByAction("CREATE");
        assertEquals(1, createEntries.size());
        assertEquals("CREATE", createEntries.get(0).action());
    }

    @Test
    void printLog_printsTableOrEmptyMessage() {
        AuditLog log = new AuditLog();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        log.printLog();
        String emptyOutput = out.toString();
        assertTrue(emptyOutput.contains("Аудит-лог пуст"));

        out.reset();
        log.log("ACTION", "user", "target", "details");
        log.printLog();
        String output = out.toString();
        assertTrue(output.contains("Аудит-лог"));
        assertTrue(output.contains("ACTION"));
    }

    @Test
    void saveToFile_writesEntries() throws Exception {
        AuditLog log = new AuditLog();
        log.log("ACTION", "user", "target", "details");

        Path tempFile = Files.createTempFile("audit-log-test", ".txt");
        try {
            log.saveToFile(tempFile.toString());
            String content = Files.readString(tempFile);
            assertTrue(content.contains("ACTION"));
            assertTrue(content.contains("user"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}

