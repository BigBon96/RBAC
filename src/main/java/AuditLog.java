import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLog {

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {
    }

    private final List<AuditEntry> entries = new ArrayList<>();

    public void log(String action, String performer, String target, String details) {
        String ts = LocalDateTime.now().toString();
        entries.add(new AuditEntry(ts, action, performer, target, details));
    }

    public List<AuditEntry> getAll() {
        return Collections.unmodifiableList(entries);
    }

    public List<AuditEntry> getByPerformer(String performer) {
        return entries.stream()
                .filter(e -> e.performer().equals(performer))
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getByAction(String action) {
        return entries.stream()
                .filter(e -> e.action().equals(action))
                .collect(Collectors.toList());
    }

    public void printLog() {
        if (entries.isEmpty()) {
            System.out.println("Аудит-лог пуст");
            return;
        }

        String[] headers = {"Timestamp", "Action", "Performer", "Target", "Details"};
        List<String[]> rows = entries.stream()
                .map(e -> new String[]{
                        e.timestamp(),
                        e.action(),
                        e.performer(),
                        e.target(),
                        e.details()
                })
                .collect(Collectors.toList());

        String table = FormatUtils.formatTable(headers, rows);
        System.out.println(FormatUtils.formatHeader("Аудит-лог"));
        System.out.println(table);
    }

    public void saveToFile(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Имя файла для аудита не должно быть пустым");
        }
        StringBuilder sb = new StringBuilder();
        for (AuditEntry e : entries) {
            sb.append(String.format("%s\t%s\t%s\t%s\t%s%n",
                    e.timestamp(),
                    e.action(),
                    e.performer(),
                    e.target(),
                    e.details().replace("\n", " ")));
        }
        try {
            Files.writeString(Path.of(filename), sb.toString());
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить аудит-лог в файл: " + ex.getMessage(), ex);
        }
    }
}

