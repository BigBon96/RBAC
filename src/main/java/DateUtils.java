import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtils {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
    }

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DATE_TIME);
    }

    public static boolean isBefore(String date1, String date2) {
        LocalDate d1 = LocalDate.parse(date1, DATE);
        LocalDate d2 = LocalDate.parse(date2, DATE);
        return d1.isBefore(d2);
    }

    public static boolean isAfter(String date1, String date2) {
        LocalDate d1 = LocalDate.parse(date1, DATE);
        LocalDate d2 = LocalDate.parse(date2, DATE);
        return d1.isAfter(d2);
    }

    public static String addDays(String date, int days) {
        LocalDate d = LocalDate.parse(date, DATE);
        return d.plusDays(days).format(DATE);
    }

    public static String formatRelativeTime(String date) {
        LocalDate target = LocalDate.parse(date, DATE);
        LocalDate today = LocalDate.now();
        long diff = ChronoUnit.DAYS.between(today, target);
        if (diff == 0) {
            return "today";
        } else if (diff > 0) {
            return "in " + diff + " day" + (diff == 1 ? "" : "s");
        } else {
            long daysAgo = Math.abs(diff);
            return daysAgo + " day" + (daysAgo == 1 ? "" : "s") + " ago";
        }
    }
}

