package utils;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple delimited-text file storage — no external JSON library required.
 * Each line in the file represents one record.
 * Fields within a record are separated by the DELIMITER character.
 */
public class FileUtil {

    public static final String DELIMITER    = "|";
    public static final String LIST_SEP     = ",";
    public static final String EMPTY_MARKER = "-";

    /** Read all non-blank, non-comment lines from a file. */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("FileUtil read error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }

    /** Write all lines to a file (overwrites). */
    public static void writeLines(String filePath, List<String> lines) {
        try {
            File f = new File(filePath);
            f.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("FileUtil write error [" + filePath + "]: " + e.getMessage());
        }
    }

    /** Append a single line to a file. */
    public static void appendLine(String filePath, String line) {
        try {
            File f = new File(filePath);
            f.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("FileUtil append error [" + filePath + "]: " + e.getMessage());
        }
    }

    /** Escape a field value so it doesn't contain the delimiter. */
    public static String esc(String value) {
        if (value == null || value.isEmpty()) return EMPTY_MARKER;
        return value.replace(DELIMITER, "\\|");
    }

    /** Un-escape a field value. */
    public static String unesc(String value) {
        if (EMPTY_MARKER.equals(value)) return "";
        return value.replace("\\|", DELIMITER);
    }

    /** Join fields into a record line. */
    public static String join(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(DELIMITER);
            sb.append(esc(fields[i]));
        }
        return sb.toString();
    }

    /** Split a record line into fields. */
    public static String[] split(String line) {
        String[] parts = line.split("\\|", -1);
        for (int i = 0; i < parts.length; i++) parts[i] = unesc(parts[i]);
        return parts;
    }

    /** Ensure a data directory and file exist. */
    public static void ensureFile(String filePath) {
        try {
            File f = new File(filePath);
            f.getParentFile().mkdirs();
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            System.err.println("FileUtil ensureFile error [" + filePath + "]: " + e.getMessage());
        }
    }
}
