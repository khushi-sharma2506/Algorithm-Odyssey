package managers;

import models.User;
import utils.FileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations for User records stored in data/users.txt
 */
public class UserManager {

    private static final String FILE = "data/users.txt";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static UserManager instance;

    private UserManager() {
        FileUtil.ensureFile(FILE);
    }

    public static UserManager getInstance() {
        if (instance == null) instance = new UserManager();
        return instance;
    }

    // ── Read all ─────────────────────────────────────────────────────────────

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (String line : FileUtil.readLines(FILE)) {
            User u = User.deserialize(line);
            if (u != null) users.add(u);
        }
        return users;
    }

    // ── Lookup ───────────────────────────────────────────────────────────────

    public User findByUsername(String username) {
        for (User u : getAllUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public User findById(String id) {
        for (User u : getAllUsers()) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    /** Find the user whose rememberToken is not empty (single active token). */
    public User findByRememberToken() {
        for (User u : getAllUsers()) {
            if (u.getRememberToken() != null && !u.getRememberToken().isEmpty()) {
                return u;
            }
        }
        return null;
    }

    // ── Create ───────────────────────────────────────────────────────────────

    public boolean createUser(User user) {
        if (findByUsername(user.getUsername()) != null) return false; // duplicate
        FileUtil.appendLine(FILE, user.serialize());
        return true;
    }

    // ── Update ───────────────────────────────────────────────────────────────

    public void updateUser(User updated) {
        List<User> users = getAllUsers();
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            if (u.getId().equals(updated.getId())) {
                lines.add(updated.serialize());
            } else {
                lines.add(u.serialize());
            }
        }
        FileUtil.writeLines(FILE, lines);
    }

    // ── Convenience ──────────────────────────────────────────────────────────

    /**
     * Award XP, persist, and return true if the user levelled up.
     */
    public boolean awardXP(User user, int xp) {
        boolean levelledUp = user.addXP(xp);
        user.setLastLogin(LocalDateTime.now().format(FMT));
        updateUser(user);
        return levelledUp;
    }

    public void completeAlgorithm(User user, String algoId, int xpReward) {
        boolean isNew = user.completeAlgorithm(algoId);
        if (isNew) user.addXP(xpReward);
        updateUser(user);
    }

    public void recordLogin(User user) {
        user.setLastLogin(LocalDateTime.now().format(FMT));
        updateUser(user);
    }

    /** Return users sorted by XP descending (leaderboard). */
    public List<User> getLeaderboard() {
        List<User> users = getAllUsers();
        users.sort((a, b) -> Integer.compare(b.getXp(), a.getXp()));
        return users;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }
}
