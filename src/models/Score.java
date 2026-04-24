package models;

import utils.FileUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Records one algorithm completion event.
 *
 * Format:
 *   userId | algorithmId | score | timeTakenSeconds | completedAt
 */
public class Score {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String userId;
    private String algorithmId;
    private int    score;
    private int    timeTakenSeconds;
    private String completedAt;

    public Score() {}

    public Score(String userId, String algorithmId, int score, int timeTakenSeconds) {
        this.userId          = userId;
        this.algorithmId     = algorithmId;
        this.score           = score;
        this.timeTakenSeconds = timeTakenSeconds;
        this.completedAt     = LocalDateTime.now().format(FMT);
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public String serialize() {
        return FileUtil.join(userId, algorithmId,
                String.valueOf(score), String.valueOf(timeTakenSeconds),
                completedAt);
    }

    public static Score deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = FileUtil.split(line);
        if (p.length < 5) return null;
        Score s = new Score();
        s.userId      = p[0];
        s.algorithmId = p[1];
        try {
            s.score           = Integer.parseInt(p[2]);
            s.timeTakenSeconds = Integer.parseInt(p[3]);
        } catch (NumberFormatException ignored) {}
        s.completedAt = p[4];
        return s;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getUserId()          { return userId; }
    public String getAlgorithmId()     { return algorithmId; }
    public int    getScore()           { return score; }
    public int    getTimeTakenSeconds(){ return timeTakenSeconds; }
    public String getCompletedAt()     { return completedAt; }
}
