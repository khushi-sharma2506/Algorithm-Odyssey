package games;

import managers.SessionManager;
import managers.UserManager;
import managers.ScoreManager;
import ui.components.RoundedButton;
import utils.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Greedy Algorithm Game Mode.
 * Tabs: Activity Selection | Coin Change | Fractional Knapsack | Job Scheduling
 */
public class GreedyGamePanel extends JPanel {

    public GreedyGamePanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("🎮 Greedy Game Mode");
        title.setFont(ThemeManager.FONT_LARGE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeManager.BG_SECONDARY);
        tabs.setForeground(ThemeManager.TEXT_PRIMARY);
        tabs.setFont(ThemeManager.FONT_NORMAL);

        tabs.addTab("📅 Activity Selection", new ActivitySelectionGame());
        tabs.addTab("🪙 Coin Change",         new CoinChangeGame());
        tabs.addTab("🎒 Fractional Knapsack", new KnapsackGame());

        add(title, BorderLayout.NORTH);
        add(tabs,  BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: award XP
    // ─────────────────────────────────────────────────────────────────────────
    static void awardXP(String id, int score, int time) {
        var sess = SessionManager.getInstance();
        if (!sess.isLoggedIn()) return;
        UserManager.getInstance().completeAlgorithm(sess.getCurrentUser(), id, 80);
        ScoreManager.getInstance().recordResult(sess.getCurrentUser().getId(), id, score, time);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 1. Activity Selection Game
    // ═════════════════════════════════════════════════════════════════════════
    static class ActivitySelectionGame extends JPanel {
        // Each activity: {start, finish}
        private int[][] activities;
        private boolean[] selected;
        private boolean[] correct;
        private JLabel feedback, scoreLabel;
        private JPanel actPanel;
        private int score = 0;
        private long startTime;

        ActivitySelectionGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            // Theory card
            JPanel theory = theory("Activity Selection",
                    "<html><b>Goal:</b> Select maximum non-overlapping activities.<br>" +
                    "Sort by finish time. Greedily pick each activity that starts<br>" +
                    "after the last selected one finishes.<br><br>" +
                    "<b>Click activities to toggle selection, then Submit.</b></html>");

            actPanel = new JPanel(new GridLayout(0, 4, 8, 8));
            actPanel.setOpaque(false);

            feedback   = statusLabel("Select activities that don't overlap.");
            scoreLabel = statusLabel("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);

            RoundedButton submitBtn = new RoundedButton("✔ Submit");
            RoundedButton newBtn    = new RoundedButton("🔀 New Round", RoundedButton.Style.SECONDARY);
            RoundedButton hintBtn   = new RoundedButton("💡 Show Answer", RoundedButton.Style.GHOST);

            submitBtn.addActionListener(e -> checkAnswer());
            newBtn.addActionListener(e -> newRound());
            hintBtn.addActionListener(e -> showAnswer());

            btnRow.add(submitBtn); btnRow.add(newBtn); btnRow.add(hintBtn);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            JScrollPane scroll = new JScrollPane(actPanel);
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));

            add(theory, BorderLayout.NORTH);
            add(scroll,  BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            newRound();
        }

        private void newRound() {
            activities = generateActivities(8);
            selected   = new boolean[activities.length];
            correct    = computeGreedy();
            startTime  = System.currentTimeMillis();
            feedback.setText("Select non-overlapping activities (greedy: sort by finish time).");
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
            buildActivityCards();
        }

        private void buildActivityCards() {
            actPanel.removeAll();
            // Header row
            for (String h : new String[]{"Activity", "Start", "Finish", "Select"}) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setFont(ThemeManager.FONT_MEDIUM);
                l.setForeground(ThemeManager.TEXT_SECONDARY);
                actPanel.add(l);
            }
            for (int i = 0; i < activities.length; i++) {
                final int idx = i;
                JLabel name = new JLabel("A" + (i+1), SwingConstants.CENTER);
                name.setFont(ThemeManager.FONT_NORMAL);
                name.setForeground(ThemeManager.TEXT_PRIMARY);

                JLabel start  = new JLabel(String.valueOf(activities[i][0]), SwingConstants.CENTER);
                JLabel finish = new JLabel(String.valueOf(activities[i][1]), SwingConstants.CENTER);
                start.setFont(ThemeManager.FONT_NORMAL);  start.setForeground(ThemeManager.TEXT_SECONDARY);
                finish.setFont(ThemeManager.FONT_NORMAL); finish.setForeground(ThemeManager.TEXT_SECONDARY);

                JCheckBox cb = new JCheckBox();
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setOpaque(false);
                cb.addActionListener(e -> { selected[idx] = cb.isSelected(); });

                actPanel.add(name); actPanel.add(start); actPanel.add(finish); actPanel.add(cb);
            }
            actPanel.revalidate(); actPanel.repaint();
        }

        private void checkAnswer() {
            boolean[] ans = computeGreedy();
            boolean perfect = Arrays.equals(selected, ans);
            int correct = countSelected(selected), maxCorrect = countSelected(ans);

            if (perfect) {
                score += 100;
                feedback.setText("✅ Perfect! Maximum " + correct + " activities selected. +100 XP");
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                awardXP("GREEDY_ACTIVITY", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                score += 30;
                feedback.setText("❌ You selected " + correct + ", but max is " + maxCorrect + ". Try again!");
                feedback.setForeground(ThemeManager.ACCENT_PINK);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void showAnswer() {
            correct = computeGreedy();
            feedback.setText("Greedy answer shown (sorted by finish time).");
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
            // Rebuild with correct highlighted
            actPanel.removeAll();
            for (String h : new String[]{"Activity","Start","Finish","Select"}) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setFont(ThemeManager.FONT_MEDIUM); l.setForeground(ThemeManager.TEXT_SECONDARY);
                actPanel.add(l);
            }
            for (int i = 0; i < activities.length; i++) {
                boolean sel = correct[i];
                JLabel name = new JLabel("A"+(i+1), SwingConstants.CENTER);
                name.setFont(ThemeManager.FONT_NORMAL);
                name.setForeground(sel ? ThemeManager.ACCENT_GREEN : ThemeManager.TEXT_PRIMARY);
                JLabel s  = new JLabel(String.valueOf(activities[i][0]), SwingConstants.CENTER);
                JLabel f  = new JLabel(String.valueOf(activities[i][1]), SwingConstants.CENTER);
                s.setFont(ThemeManager.FONT_NORMAL); s.setForeground(ThemeManager.TEXT_SECONDARY);
                f.setFont(ThemeManager.FONT_NORMAL); f.setForeground(ThemeManager.TEXT_SECONDARY);
                JLabel check = new JLabel(sel ? "✅" : "—", SwingConstants.CENTER);
                check.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                actPanel.add(name); actPanel.add(s); actPanel.add(f); actPanel.add(check);
            }
            actPanel.revalidate(); actPanel.repaint();
        }

        private boolean[] computeGreedy() {
            int n = activities.length;
            Integer[] idx = new Integer[n];
            for (int i = 0; i < n; i++) idx[i] = i;
            Arrays.sort(idx, Comparator.comparingInt(i -> activities[i][1]));
            boolean[] sel = new boolean[n];
            int lastFinish = 0;
            for (int i : idx) {
                if (activities[i][0] >= lastFinish) {
                    sel[i] = true;
                    lastFinish = activities[i][1];
                }
            }
            return sel;
        }

        private int[][] generateActivities(int n) {
            Random rnd = new Random();
            int[][] acts = new int[n][2];
            for (int i = 0; i < n; i++) {
                int s = rnd.nextInt(10);
                acts[i] = new int[]{s, s + rnd.nextInt(5) + 1};
            }
            return acts;
        }

        private int countSelected(boolean[] sel) {
            int c = 0; for (boolean b : sel) if (b) c++; return c;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 2. Coin Change Game
    // ═════════════════════════════════════════════════════════════════════════
    static class CoinChangeGame extends JPanel {
        private int[] coins = {1, 5, 10, 25, 50};
        private int target, score = 0;
        private int[] userChoice;
        private JLabel feedback, scoreLabel, targetLabel;
        private JSpinner[] spinners;
        private long startTime;

        CoinChangeGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JPanel theory = theory("Greedy Coin Change",
                    "<html><b>Goal:</b> Make change for the target using the fewest coins.<br>" +
                    "Greedy: always pick the largest coin ≤ remaining amount.<br><br>" +
                    "<b>Enter how many of each coin to use, then Submit.</b></html>");

            targetLabel = new JLabel("Target: ₹0", SwingConstants.CENTER);
            targetLabel.setFont(ThemeManager.FONT_LARGE);
            targetLabel.setForeground(ThemeManager.ACCENT_YELLOW);
            targetLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

            JPanel coinPanel = new JPanel(new GridLayout(2, 5, 10, 8));
            coinPanel.setOpaque(false);
            spinners = new JSpinner[coins.length];
            for (int i = 0; i < coins.length; i++) {
                JLabel cl = new JLabel("₹" + coins[i], SwingConstants.CENTER);
                cl.setFont(ThemeManager.FONT_MEDIUM);
                cl.setForeground(ThemeManager.ACCENT_CYAN);
                coinPanel.add(cl);
            }
            for (int i = 0; i < coins.length; i++) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
                spinners[i].setFont(ThemeManager.FONT_NORMAL);
                coinPanel.add(spinners[i]);
            }

            feedback   = statusLabel("Enter coin counts and submit.");
            scoreLabel = statusLabel("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);
            RoundedButton submitBtn = new RoundedButton("✔ Submit");
            RoundedButton newBtn    = new RoundedButton("🔀 New Round", RoundedButton.Style.SECONDARY);
            RoundedButton hintBtn   = new RoundedButton("💡 Show Answer", RoundedButton.Style.GHOST);
            submitBtn.addActionListener(e -> checkAnswer());
            newBtn.addActionListener(e -> newRound());
            hintBtn.addActionListener(e -> showAnswer());
            btnRow.add(submitBtn); btnRow.add(newBtn); btnRow.add(hintBtn);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            add(theory,   BorderLayout.NORTH);
            JPanel centre = new JPanel(new BorderLayout(0, 10));
            centre.setOpaque(false);
            centre.add(targetLabel, BorderLayout.NORTH);
            centre.add(coinPanel,   BorderLayout.CENTER);
            add(centre, BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            newRound();
        }

        private void newRound() {
            target = new Random().nextInt(95) + 5;
            targetLabel.setText("Make Change For: " + target + " cents");
            for (JSpinner s : spinners) s.setValue(0);
            feedback.setText("Enter how many of each coin to use.");
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
            startTime = System.currentTimeMillis();
        }

        private void checkAnswer() {
            int total = 0, numCoins = 0;
            userChoice = new int[coins.length];
            for (int i = 0; i < coins.length; i++) {
                userChoice[i] = (int) spinners[i].getValue();
                total    += userChoice[i] * coins[i];
                numCoins += userChoice[i];
            }
            int[] greedy = computeGreedy();
            int greedyCoins = Arrays.stream(greedy).sum();
            if (total != target) {
                feedback.setText("❌ Total is " + total + ", not " + target + ". Try again.");
                feedback.setForeground(ThemeManager.ERROR);
            } else if (numCoins == greedyCoins) {
                score += 100;
                feedback.setText("✅ Optimal! Used " + numCoins + " coins. +100 XP");
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                awardXP("GREEDY_COIN", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                score += 40;
                feedback.setText("✔ Correct total but not optimal (" + numCoins + " vs " + greedyCoins + "). +40");
                feedback.setForeground(ThemeManager.ACCENT_YELLOW);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void showAnswer() {
            int[] g = computeGreedy();
            StringBuilder sb = new StringBuilder("Greedy: ");
            for (int i = coins.length - 1; i >= 0; i--) {
                if (g[i] > 0) sb.append(g[i]).append("×₹").append(coins[i]).append("  ");
            }
            feedback.setText(sb.toString());
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
        }

        private int[] computeGreedy() {
            int[] cnt = new int[coins.length];
            int rem = target;
            for (int i = coins.length - 1; i >= 0 && rem > 0; i--) {
                cnt[i] = rem / coins[i];
                rem    -= cnt[i] * coins[i];
            }
            return cnt;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 3. Fractional Knapsack
    // ═════════════════════════════════════════════════════════════════════════
    static class KnapsackGame extends JPanel {
        private int[][] items; // {weight, value}
        private int capacity;
        private JCheckBox[] checks;
        private JLabel feedback, scoreLabel, capLabel;
        private int score = 0;
        private long startTime;
        private JPanel itemPanel;

        KnapsackGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JPanel theory = theory("Fractional Knapsack",
                    "<html><b>Goal:</b> Maximise value within weight capacity.<br>" +
                    "Greedy: Sort items by value/weight ratio, take highest first.<br><br>" +
                    "<b>Select items to put in the knapsack, then Submit.</b></html>");

            capLabel  = statusLabel("Capacity: 0 kg");
            capLabel.setFont(ThemeManager.FONT_LARGE);
            capLabel.setForeground(ThemeManager.ACCENT_YELLOW);

            itemPanel = new JPanel(new GridLayout(0, 5, 8, 8));
            itemPanel.setOpaque(false);

            feedback   = statusLabel("Select items and submit.");
            scoreLabel = statusLabel("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);
            RoundedButton submitBtn = new RoundedButton("✔ Submit");
            RoundedButton newBtn    = new RoundedButton("🔀 New Round", RoundedButton.Style.SECONDARY);
            RoundedButton hintBtn   = new RoundedButton("💡 Show Answer", RoundedButton.Style.GHOST);
            submitBtn.addActionListener(e -> checkAnswer());
            newBtn.addActionListener(e -> newRound());
            hintBtn.addActionListener(e -> showAnswer());
            btnRow.add(submitBtn); btnRow.add(newBtn); btnRow.add(hintBtn);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            JScrollPane scroll = new JScrollPane(itemPanel);
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));

            add(theory,   BorderLayout.NORTH);
            JPanel centre = new JPanel(new BorderLayout(0, 8));
            centre.setOpaque(false);
            centre.add(capLabel, BorderLayout.NORTH);
            centre.add(scroll,   BorderLayout.CENTER);
            add(centre, BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            newRound();
        }

        private void newRound() {
            Random rnd = new Random();
            int n = 6 + rnd.nextInt(3);
            items    = new int[n][2];
            capacity = 15 + rnd.nextInt(10);
            for (int i = 0; i < n; i++) items[i] = new int[]{1+rnd.nextInt(8), 5+rnd.nextInt(20)};
            capLabel.setText("Capacity: " + capacity + " kg");
            startTime = System.currentTimeMillis();
            buildItemCards();
            feedback.setText("Select items. Total weight must not exceed capacity.");
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
        }

        private void buildItemCards() {
            itemPanel.removeAll();
            checks = new JCheckBox[items.length];
            for (String h : new String[]{"Item","Weight","Value","Ratio","Select"}) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setFont(ThemeManager.FONT_MEDIUM); l.setForeground(ThemeManager.TEXT_SECONDARY);
                itemPanel.add(l);
            }
            for (int i = 0; i < items.length; i++) {
                double ratio = (double) items[i][1] / items[i][0];
                JLabel name  = lbl("I" + (i+1));
                JLabel wt    = lbl(items[i][0] + " kg");
                JLabel val   = lbl("₹" + items[i][1]);
                JLabel rat   = lbl(String.format("%.2f", ratio));
                checks[i]    = new JCheckBox();
                checks[i].setHorizontalAlignment(SwingConstants.CENTER);
                checks[i].setOpaque(false);
                itemPanel.add(name); itemPanel.add(wt); itemPanel.add(val);
                itemPanel.add(rat); itemPanel.add(checks[i]);
            }
            itemPanel.revalidate(); itemPanel.repaint();
        }

        private void checkAnswer() {
            int totalW = 0, totalV = 0;
            for (int i = 0; i < items.length; i++) {
                if (checks[i].isSelected()) { totalW += items[i][0]; totalV += items[i][1]; }
            }
            if (totalW > capacity) {
                feedback.setText("❌ Over capacity! (" + totalW + " > " + capacity + " kg)");
                feedback.setForeground(ThemeManager.ERROR); return;
            }
            int optVal = computeGreedyValue();
            if (totalV >= optVal) {
                score += 100;
                feedback.setText("✅ Optimal! Value = ₹" + totalV + ", Weight = " + totalW + "kg. +100");
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                awardXP("GREEDY_KNAPSACK", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                score += 40;
                feedback.setText("✔ Valid but not optimal (₹" + totalV + " vs ₹" + optVal + "). +40");
                feedback.setForeground(ThemeManager.ACCENT_YELLOW);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void showAnswer() {
            Integer[] idx = new Integer[items.length];
            for (int i = 0; i < items.length; i++) idx[i] = i;
            Arrays.sort(idx, (a, b) -> Double.compare((double)items[b][1]/items[b][0], (double)items[a][1]/items[a][0]));
            int rem = capacity;
            Set<Integer> sel = new HashSet<>();
            for (int i : idx) { if (items[i][0] <= rem) { sel.add(i); rem -= items[i][0]; } }
            feedback.setText("Greedy picks: " + sel.stream().map(i -> "I"+(i+1)).toList());
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
        }

        private int computeGreedyValue() {
            Integer[] idx = new Integer[items.length];
            for (int i = 0; i < items.length; i++) idx[i] = i;
            Arrays.sort(idx, (a, b) -> Double.compare((double)items[b][1]/items[b][0], (double)items[a][1]/items[a][0]));
            int rem = capacity, val = 0;
            for (int i : idx) { if (items[i][0] <= rem) { val += items[i][1]; rem -= items[i][0]; } }
            return val;
        }

        private JLabel lbl(String t) {
            JLabel l = new JLabel(t, SwingConstants.CENTER);
            l.setFont(ThemeManager.FONT_NORMAL); l.setForeground(ThemeManager.TEXT_PRIMARY);
            return l;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shared helpers
    // ─────────────────────────────────────────────────────────────────────────

    static JPanel theory(String heading, String html) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel h = new JLabel(heading);
        h.setFont(ThemeManager.FONT_MEDIUM); h.setForeground(ThemeManager.ACCENT);
        JLabel body = new JLabel(html);
        body.setFont(ThemeManager.FONT_SMALL); body.setForeground(ThemeManager.TEXT_SECONDARY);
        p.add(h, BorderLayout.NORTH); p.add(body, BorderLayout.CENTER);
        return p;
    }

    static JLabel statusLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        return l;
    }
}
