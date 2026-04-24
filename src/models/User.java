package models;

import utils.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents an AlgoVerse user account.
 * Serialised to a single pipe-delimited line in data/users.txt
 *
 * Format:
 *   id | username | passwordHash | xp | level | streak |
 *   completedAlgos (csv) | lastLogin | rememberToken
 */
public class User {

    // ── Stored fields ────────────────────────────────────────────────────────
    private String id;
    private String username;
    private String passwordHash;
    private int    xp;
    private int    level;
    private int    streak;
    private List<String> completedAlgorithms;
    private String lastLogin;
    private String rememberToken;

    // ── XP thresholds (level = floor(xp/XP_PER_LEVEL) + 1) ─────────────────
    public static final int XP_PER_LEVEL = 500;

    // ── Constructors ─────────────────────────────────────────────────────────

    public User() {
        completedAlgorithms = new ArrayList<>();
    }

    /** Create a brand-new user (registration). */
    public User(String username, String passwordHash) {
        this.id            = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        this.username      = username;
        this.passwordHash  = passwordHash;
        this.xp            = 0;
        this.level         = 1;
        this.streak        = 0;
        this.completedAlgorithms = new ArrayList<>();
        this.lastLogin     = "";
        this.rememberToken = "";
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public String serialize() {
        String algos = completedAlgorithms.isEmpty() ? "" :
                       String.join(FileUtil.LIST_SEP, completedAlgorithms);
        return FileUtil.join(
            id, username, passwordHash,
            String.valueOf(xp), String.valueOf(level), String.valueOf(streak),
            algos, lastLogin, rememberToken
        );
    }

    public static User deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = FileUtil.split(line);
        if (p.length < 9) return null;
        User u = new User();
        u.id            = p[0];
        u.username      = p[1];
        u.passwordHash  = p[2];
        try {
            u.xp     = Integer.parseInt(p[3]);
            u.level  = Integer.parseInt(p[4]);
            u.streak = Integer.parseInt(p[5]);
        } catch (NumberFormatException e) { /* default 0 */ }
        u.completedAlgorithms = new ArrayList<>();
        if (!p[6].isEmpty()) {
            u.completedAlgorithms.addAll(Arrays.asList(p[6].split(",")));
        }
        u.lastLogin     = p[7];
        u.rememberToken = p.length > 8 ? p[8] : "";
        return u;
    }

    // ── Business logic ───────────────────────────────────────────────────────

    /** Add XP and auto-adjust level. Returns true if levelled up. */
    public boolean addXP(int amount) {
        int prevLevel = this.level;
        this.xp      += amount;
        this.level    = (this.xp / XP_PER_LEVEL) + 1;
        return this.level > prevLevel;
    }

    /** Mark an algorithm as completed (idempotent). Returns true if new. */
    public boolean completeAlgorithm(String algoId) {
        if (!completedAlgorithms.contains(algoId)) {
            completedAlgorithms.add(algoId);
            return true;
        }
        return false;
    }

    public boolean hasCompleted(String algoId) {
        return completedAlgorithms.contains(algoId);
    }

    /** XP earned within the current level (for progress bar). */
    public int getXpInCurrentLevel() {
        return xp % XP_PER_LEVEL;
    }

    /** XP required to reach next level. */
    public int getXpForNextLevel() {
        return XP_PER_LEVEL;
    }

    /** Progress 0.0–1.0 within current level. */
    public float getLevelProgress() {
        return (float) getXpInCurrentLevel() / XP_PER_LEVEL;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()            { return id; }
    public String getUsername()      { return username; }
    public String getPasswordHash()  { return passwordHash; }
    public int    getXp()            { return xp; }
    public int    getLevel()         { return level; }
    public int    getStreak()        { return streak; }
    public List<String> getCompletedAlgorithms() { return completedAlgorithms; }
    public String getLastLogin()     { return lastLogin; }
    public String getRememberToken() { return rememberToken; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setXp(int xp)                    { this.xp = xp; }
    public void setLevel(int level)              { this.level = level; }
    public void setStreak(int streak)            { this.streak = streak; }
    public void setLastLogin(String lastLogin)   { this.lastLogin = lastLogin; }
    public void setRememberToken(String token)   { this.rememberToken = token; }
    public void setPasswordHash(String hash)     { this.passwordHash = hash; }

    @Override public String toString() { return username; }
}
