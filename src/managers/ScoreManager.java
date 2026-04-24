package managers;

import models.Score;
import utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations for Score records stored in data/scores.txt
 */
public class ScoreManager {

    private static final String FILE = "data/scores.txt";
    private static ScoreManager instance;

    private ScoreManager() {
        FileUtil.ensureFile(FILE);
    }

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }

    /** Persist a new score entry. */
    public void addScore(Score score) {
        FileUtil.appendLine(FILE, score.serialize());
    }

    /** All scores ever recorded. */
    public List<Score> getAllScores() {
        List<Score> list = new ArrayList<>();
        for (String line : FileUtil.readLines(FILE)) {
            Score s = Score.deserialize(line);
            if (s != null) list.add(s);
        }
        return list;
    }

    /** Scores for a specific user. */
    public List<Score> getScoresForUser(String userId) {
        List<Score> out = new ArrayList<>();
        for (Score s : getAllScores()) {
            if (s.getUserId().equals(userId)) out.add(s);
        }
        return out;
    }

    /** Highest score a user achieved for a given algorithm. */
    public int getBestScore(String userId, String algorithmId) {
        int best = 0;
        for (Score s : getScoresForUser(userId)) {
            if (s.getAlgorithmId().equals(algorithmId) && s.getScore() > best) {
                best = s.getScore();
            }
        }
        return best;
    }

    /** Record a game result and award XP to the current session user. */
    public void recordResult(String userId, String algorithmId,
                             int score, int timeTakenSeconds) {
        Score s = new Score(userId, algorithmId, score, timeTakenSeconds);
        addScore(s);
    }
}
