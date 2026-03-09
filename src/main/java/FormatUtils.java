import java.util.ArrayList;
import java.util.List;

public final class FormatUtils {

    private FormatUtils() {
    }

    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            return "";
        }
        int cols = headers.length;
        List<String[]> safeRows = rows != null ? rows : new ArrayList<>();

        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) {
            widths[i] = headers[i] != null ? headers[i].length() : 0;
        }
        for (String[] row : safeRows) {
            if (row == null) continue;
            for (int i = 0; i < cols && i < row.length; i++) {
                String cell = row[i] == null ? "" : row[i];
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        String horizontal = buildHorizontalBorder(widths);
        sb.append(horizontal).append(System.lineSeparator());
        sb.append(buildRow(headers, widths)).append(System.lineSeparator());
        sb.append(horizontal).append(System.lineSeparator());
        for (String[] row : safeRows) {
            sb.append(buildRow(row, widths)).append(System.lineSeparator());
        }
        sb.append(horizontal);
        return sb.toString();
    }

    private static String buildHorizontalBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('+');
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append('+');
        }
        return sb.toString();
    }

    private static String buildRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int i = 0; i < widths.length; i++) {
            String cell = (cells != null && i < cells.length && cells[i] != null) ? cells[i] : "";
            sb.append(' ').append(padRight(cell, widths[i])).append(' ').append('|');
        }
        return sb.toString();
    }

    public static String formatBox(String text) {
        String normalized = text == null ? "" : text;
        String[] lines = normalized.split("\\R");
        int maxLen = 0;
        for (String line : lines) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }
        String border = "+" + "-".repeat(maxLen + 2) + "+";
        StringBuilder sb = new StringBuilder();
        sb.append(border).append(System.lineSeparator());
        for (String line : lines) {
            sb.append("| ")
                    .append(padRight(line, maxLen))
                    .append(" |")
                    .append(System.lineSeparator());
        }
        sb.append(border);
        return sb.toString();
    }

    public static String formatHeader(String text) {
        String normalized = text == null ? "" : text;
        String line = "-".repeat(normalized.length());
        return normalized + System.lineSeparator() + line;
    }

    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (maxLength <= 0) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 3) {
            return ".".repeat(maxLength);
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String padRight(String text, int length) {
        if (length <= 0) {
            return "";
        }
        String value = text == null ? "" : text;
        if (value.length() >= length) {
            return value;
        }
        return value + " ".repeat(length - value.length());
    }

    public static String padLeft(String text, int length) {
        if (length <= 0) {
            return "";
        }
        String value = text == null ? "" : text;
        if (value.length() >= length) {
            return value;
        }
        return " ".repeat(length - value.length()) + value;
    }
}

