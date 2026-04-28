package games;
import managers.SessionManager;
import managers.UserManager;
import managers.ScoreManager;
import ui.components.RoundedButton;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Dynamic Programming Game Mode.
 * Tabs: Fibonacci DP | 0/1 Knapsack | LCS
 */
public class DPGamePanel extends JPanel {

    public DPGamePanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("🧩 Dynamic Programming Game Mode");
        title.setFont(ThemeManager.FONT_LARGE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeManager.BG_SECONDARY);
        tabs.setForeground(ThemeManager.TEXT_PRIMARY);
        tabs.setFont(ThemeManager.FONT_NORMAL);

        tabs.addTab("🔢 Fibonacci DP",    new FibonacciDPGame());
        tabs.addTab("🎒 0/1 Knapsack DP", new KnapsackDPGame());
        tabs.addTab("📝 LCS",             new LCSGame());

        add(title, BorderLayout.NORTH);
        add(tabs,  BorderLayout.CENTER);
    }

    // ─── shared award helper ──────────────────────────────────────────────────
    static void award(String id, int score, int timeSec) {
        var sess = SessionManager.getInstance();
        if (!sess.isLoggedIn()) return;
        UserManager.getInstance().completeAlgorithm(sess.getCurrentUser(), id, 90);
        ScoreManager.getInstance().recordResult(sess.getCurrentUser().getId(), id, score, timeSec);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 1. Fibonacci DP  — fill in the table step by step
    // ═════════════════════════════════════════════════════════════════════════
    static class FibonacciDPGame extends JPanel {

        private static final int N = 10;
        private final int[] fib  = new int[N];
        private final JTextField[] cells = new JTextField[N];
        private JLabel feedback, scoreLabel;
        private int score = 0;
        private long startTime;

        FibonacciDPGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            // Theory
            JPanel theory = theoryCard("Fibonacci with Memoisation",
                "<html><b>DP recurrence:</b>  F(n) = F(n-1) + F(n-2) ,  F(0)=0 , F(1)=1<br>" +
                "Fill in the table from left to right using the recurrence.</html>");

            // Row labels
            JPanel topRow = new JPanel(new GridLayout(1, N, 6, 0));
            topRow.setOpaque(false);
            for (int i = 0; i < N; i++) {
                JLabel l = new JLabel("F(" + i + ")", SwingConstants.CENTER);
                l.setFont(ThemeManager.FONT_SMALL); l.setForeground(ThemeManager.TEXT_MUTED);
                topRow.add(l);
            }

            // Input cells
            JPanel cellRow = new JPanel(new GridLayout(1, N, 6, 0));
            cellRow.setOpaque(false);
            for (int i = 0; i < N; i++) {
                cells[i] = new JTextField();
                cells[i].setHorizontalAlignment(SwingConstants.CENTER);
                cells[i].setBackground(ThemeManager.BG_SURFACE);
                cells[i].setForeground(ThemeManager.TEXT_PRIMARY);
                cells[i].setCaretColor(ThemeManager.ACCENT);
                cells[i].setFont(ThemeManager.FONT_MEDIUM);
                cells[i].setPreferredSize(new Dimension(64, 38));
                cells[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(6, 4, 6, 4)));
                cellRow.add(cells[i]);
            }

            JPanel tablePanel = new JPanel(new GridLayout(2, 1, 0, 4));
            tablePanel.setOpaque(false);
            tablePanel.add(topRow);
            tablePanel.add(cellRow);

            // Wrap so tablePanel doesn't stretch to fill BorderLayout.CENTER
            JPanel tableWrapper = new JPanel(new BorderLayout());
            tableWrapper.setOpaque(false);
            tableWrapper.add(tablePanel, BorderLayout.NORTH);

            feedback   = lbl("Fill in F(0)=0, F(1)=1, then compute F(2)…F(9).");
            scoreLabel = lbl("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);
            RoundedButton submit = new RoundedButton("✔ Check");
            RoundedButton reset  = new RoundedButton("↺ Reset", RoundedButton.Style.SECONDARY);
            RoundedButton hint   = new RoundedButton("💡 Fill Answer", RoundedButton.Style.GHOST);
            submit.addActionListener(e -> check());
            reset.addActionListener(e  -> reset());
            hint.addActionListener(e   -> fillAnswer());
            btnRow.add(submit); btnRow.add(reset); btnRow.add(hint);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            add(theory,       BorderLayout.NORTH);
            add(tableWrapper, BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            buildFib();
            startTime = System.currentTimeMillis();
        }

        private void buildFib() {
            fib[0] = 0; fib[1] = 1;
            for (int i = 2; i < N; i++) fib[i] = fib[i-1] + fib[i-2];
        }

        private void check() {
            int correct = 0;
            for (int i = 0; i < N; i++) {
                try {
                    int val = Integer.parseInt(cells[i].getText().trim());
                    if (val == fib[i]) {
                        cells[i].setBackground(new Color(30, 90, 55));
                        correct++;
                    } else {
                        cells[i].setBackground(new Color(110, 35, 42));
                    }
                } catch (NumberFormatException ex) {
                    cells[i].setBackground(new Color(110, 35, 42));
                }
            }
            int earned = correct * 10;
            score += earned;
            if (correct == N) {
                feedback.setText("✅ Perfect! All " + N + " values correct! +" + earned + " XP");
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                award("DP_FIBONACCI", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                feedback.setText("❌ " + correct + "/" + N + " correct. Red cells are wrong. +" + earned);
                feedback.setForeground(ThemeManager.ACCENT_PINK);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void reset() {
            for (JTextField c : cells) {
                c.setText("");
                c.setBackground(ThemeManager.BG_SURFACE);
            }
            feedback.setText("Fill in F(0)=0, F(1)=1, then compute F(2)…F(9).");
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
            startTime = System.currentTimeMillis();
        }

        private void fillAnswer() {
            for (int i = 0; i < N; i++) {
                cells[i].setText(String.valueOf(fib[i]));
                cells[i].setBackground(new Color(50, 46, 120));
            }
            feedback.setText("Answer shown. Study the pattern!");
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 2. 0/1 Knapsack DP Table
    // ═════════════════════════════════════════════════════════════════════════
    static class KnapsackDPGame extends JPanel {

        private int[] weights, values;
        private int capacity, n;
        private int[][] dp;
        private JTextField[][] cells;
        private JLabel feedback, scoreLabel;
        private JPanel tablePanel;
        private int score = 0;
        private long startTime;

        KnapsackDPGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JPanel theory = theoryCard("0/1 Knapsack DP",
                "<html><b>Recurrence:</b><br>" +
                "dp[i][w] = max(dp[i-1][w],  dp[i-1][w-wt[i]] + val[i])  if wt[i] ≤ w<br>" +
                "dp[i][w] = dp[i-1][w]   otherwise<br><br>" +
                "Fill the DP table row by row.</html>");

            tablePanel = new JPanel();
            tablePanel.setOpaque(false);

            feedback   = lbl("Click 'New Round' to start.");
            scoreLabel = lbl("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);
            RoundedButton newBtn  = new RoundedButton("🔀 New Round");
            RoundedButton check   = new RoundedButton("✔ Check", RoundedButton.Style.SECONDARY);
            RoundedButton hint    = new RoundedButton("💡 Show Answer", RoundedButton.Style.GHOST);
            newBtn.addActionListener(e  -> newRound());
            check.addActionListener(e   -> check());
            hint.addActionListener(e    -> fillAnswer());
            btnRow.add(newBtn); btnRow.add(check); btnRow.add(hint);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            add(theory,   BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(tablePanel);
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
            add(scroll, BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            newRound();
        }

        private void newRound() {
            n        = 3 + new Random().nextInt(2); // 3 or 4 items
            capacity = 5 + new Random().nextInt(4);
            weights  = new int[n]; values = new int[n];
            Random rnd = new Random();
            for (int i = 0; i < n; i++) {
                weights[i] = 1 + rnd.nextInt(capacity);
                values[i]  = 2 + rnd.nextInt(10);
            }
            computeDP();
            buildTable();
            startTime = System.currentTimeMillis();
            feedback.setText("Fill dp[i][w] for i=0…" + n + " , w=0…" + capacity);
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
        }

        private void computeDP() {
            dp = new int[n+1][capacity+1];
            for (int i = 1; i <= n; i++)
                for (int w = 0; w <= capacity; w++) {
                    dp[i][w] = dp[i-1][w];
                    if (weights[i-1] <= w)
                        dp[i][w] = Math.max(dp[i][w], dp[i-1][w - weights[i-1]] + values[i-1]);
                }
        }

        private void buildTable() {
            tablePanel.removeAll();
            tablePanel.setLayout(new GridLayout(n+2, capacity+2, 4, 4));

            // Item info header
            JLabel corner = new JLabel("Item→Cap", SwingConstants.CENTER);
            corner.setFont(ThemeManager.FONT_SMALL); corner.setForeground(ThemeManager.TEXT_MUTED);
            tablePanel.add(corner);
            for (int w = 0; w <= capacity; w++) {
                JLabel h = new JLabel("W=" + w, SwingConstants.CENTER);
                h.setFont(ThemeManager.FONT_SMALL); h.setForeground(ThemeManager.ACCENT_CYAN);
                tablePanel.add(h);
            }

            cells = new JTextField[n+1][capacity+1];
            for (int i = 0; i <= n; i++) {
                String rowLbl = i == 0 ? "∅" : "I" + i + "(w=" + weights[i-1] + ",v=" + values[i-1] + ")";
                JLabel rl = new JLabel(rowLbl, SwingConstants.CENTER);
                rl.setFont(ThemeManager.FONT_SMALL); rl.setForeground(ThemeManager.ACCENT_YELLOW);
                tablePanel.add(rl);
                for (int w = 0; w <= capacity; w++) {
                    JTextField tf = new JTextField(i == 0 ? "0" : "");
                    tf.setHorizontalAlignment(SwingConstants.CENTER);
                    tf.setBackground(i == 0 ? ThemeManager.BG_SECONDARY : ThemeManager.BG_SURFACE);
                    tf.setForeground(ThemeManager.TEXT_PRIMARY);
                    tf.setFont(ThemeManager.FONT_NORMAL);
                    tf.setEditable(i != 0);
                    tf.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER, 1));
                    cells[i][w] = tf;
                    tablePanel.add(tf);
                }
            }
            tablePanel.revalidate(); tablePanel.repaint();
        }

        private void check() {
            int correct = 0, total = 0;
            for (int i = 1; i <= n; i++) {
                for (int w = 0; w <= capacity; w++) {
                    total++;
                    try {
                        int val = Integer.parseInt(cells[i][w].getText().trim());
                        if (val == dp[i][w]) {
                            cells[i][w].setBackground(new Color(30,90,55)); correct++;
                        } else cells[i][w].setBackground(new Color(110,35,42));
                    } catch (NumberFormatException ex) {
                        cells[i][w].setBackground(new Color(110,35,42));
                    }
                }
            }
            int earned = (int)(100.0 * correct / total);
            score += earned;
            if (correct == total) {
                feedback.setText("✅ Perfect! Max value = " + dp[n][capacity] + ". +" + earned);
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                award("DP_KNAPSACK", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                feedback.setText(correct + "/" + total + " cells correct. Red = wrong. +" + earned);
                feedback.setForeground(ThemeManager.ACCENT_PINK);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void fillAnswer() {
            for (int i = 0; i <= n; i++)
                for (int w = 0; w <= capacity; w++) {
                    cells[i][w].setText(String.valueOf(dp[i][w]));
                    cells[i][w].setBackground(new Color(50,46,120));
                }
            tablePanel.repaint();
            feedback.setText("Answer shown. Optimal value = " + dp[n][capacity]);
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 3. LCS Game
    // ═════════════════════════════════════════════════════════════════════════
    static class LCSGame extends JPanel {

        private String s1, s2;
        private int[][] dp;
        private JTextField[][] cells;
        private JLabel feedback, scoreLabel, strLabel;
        private JPanel tablePanel;
        private int score = 0;
        private long startTime;

        LCSGame() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JPanel theory = theoryCard("Longest Common Subsequence",
                "<html><b>Recurrence:</b><br>" +
                "dp[i][j] = dp[i-1][j-1] + 1    if s1[i]==s2[j]<br>" +
                "dp[i][j] = max(dp[i-1][j], dp[i][j-1])    otherwise<br>" +
                "Answer is dp[m][n].</html>");

            strLabel  = lbl("Strings: ...");
            strLabel.setFont(ThemeManager.FONT_MEDIUM);
            strLabel.setForeground(ThemeManager.ACCENT_CYAN);

            tablePanel = new JPanel();
            tablePanel.setOpaque(false);

            feedback   = lbl("Click 'New Round' to start.");
            scoreLabel = lbl("Score: 0");

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setOpaque(false);
            RoundedButton newBtn = new RoundedButton("🔀 New Round");
            RoundedButton check  = new RoundedButton("✔ Check", RoundedButton.Style.SECONDARY);
            RoundedButton hint   = new RoundedButton("💡 Show Answer", RoundedButton.Style.GHOST);
            newBtn.addActionListener(e -> newRound());
            check.addActionListener(e  -> check());
            hint.addActionListener(e   -> fillAnswer());
            btnRow.add(newBtn); btnRow.add(check); btnRow.add(hint);
            btnRow.add(Box.createHorizontalStrut(20)); btnRow.add(scoreLabel);

            add(theory,  BorderLayout.NORTH);
            JPanel mid = new JPanel(new BorderLayout(0, 6));
            mid.setOpaque(false);
            mid.add(strLabel, BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(tablePanel);
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
            mid.add(scroll, BorderLayout.CENTER);
            add(mid, BorderLayout.CENTER);
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(feedback, BorderLayout.NORTH);
            south.add(btnRow,   BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            newRound();
        }

        private void newRound() {
            String[] pool = {"ABCBDAB","BDCAB","AGGTAB","GXTXAYB","ABCDE","ACE"};
            Random rnd = new Random();
            s1 = pool[rnd.nextInt(pool.length)];
            s2 = pool[rnd.nextInt(pool.length)];
            strLabel.setText("S1 = \"" + s1 + "\"   |   S2 = \"" + s2 + "\"");
            computeLCS();
            buildTable();
            startTime = System.currentTimeMillis();
            feedback.setText("Fill the DP table. LCS length = dp[" + s1.length() + "][" + s2.length() + "]");
            feedback.setForeground(ThemeManager.TEXT_SECONDARY);
        }

        private void computeLCS() {
            int m = s1.length(), n = s2.length();
            dp = new int[m+1][n+1];
            for (int i = 1; i <= m; i++)
                for (int j = 1; j <= n; j++)
                    dp[i][j] = s1.charAt(i-1) == s2.charAt(j-1)
                            ? dp[i-1][j-1] + 1
                            : Math.max(dp[i-1][j], dp[i][j-1]);
        }

        private void buildTable() {
            tablePanel.removeAll();
            int m = s1.length(), n = s2.length();
            tablePanel.setLayout(new GridLayout(m+2, n+2, 3, 3));

            // Header row
            tablePanel.add(new JLabel("", SwingConstants.CENTER));
            JLabel eh = new JLabel("∅", SwingConstants.CENTER);
            eh.setFont(ThemeManager.FONT_SMALL); eh.setForeground(ThemeManager.TEXT_MUTED);
            tablePanel.add(eh);
            for (int j = 0; j < n; j++) {
                JLabel h = new JLabel(String.valueOf(s2.charAt(j)), SwingConstants.CENTER);
                h.setFont(ThemeManager.FONT_MEDIUM); h.setForeground(ThemeManager.ACCENT_CYAN);
                tablePanel.add(h);
            }

            cells = new JTextField[m+1][n+1];
            for (int i = 0; i <= m; i++) {
                JLabel rl = new JLabel(i == 0 ? "∅" : String.valueOf(s1.charAt(i-1)), SwingConstants.CENTER);
                rl.setFont(ThemeManager.FONT_MEDIUM);
                rl.setForeground(i == 0 ? ThemeManager.TEXT_MUTED : ThemeManager.ACCENT_YELLOW);
                tablePanel.add(rl);
                for (int j = 0; j <= n; j++) {
                    JTextField tf = new JTextField(i == 0 || j == 0 ? "0" : "");
                    tf.setHorizontalAlignment(SwingConstants.CENTER);
                    tf.setBackground(i == 0 || j == 0 ? ThemeManager.BG_SECONDARY : ThemeManager.BG_SURFACE);
                    tf.setForeground(ThemeManager.TEXT_PRIMARY);
                    tf.setFont(ThemeManager.FONT_NORMAL);
                    tf.setEditable(i != 0 && j != 0);
                    tf.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER, 1));
                    cells[i][j] = tf;
                    tablePanel.add(tf);
                }
            }
            tablePanel.revalidate(); tablePanel.repaint();
        }

        private void check() {
            int m = s1.length(), n = s2.length();
            int correct = 0, total = m * n;
            for (int i = 1; i <= m; i++)
                for (int j = 1; j <= n; j++) {
                    try {
                        int v = Integer.parseInt(cells[i][j].getText().trim());
                        if (v == dp[i][j]) { cells[i][j].setBackground(new Color(30,90,55)); correct++; }
                        else                  cells[i][j].setBackground(new Color(110,35,42));
                    } catch (NumberFormatException ex) {
                        cells[i][j].setBackground(new Color(110,35,42));
                    }
                }
            int earned = (int)(100.0 * correct / total);
            score += earned;
            if (correct == total) {
                feedback.setText("✅ Perfect! LCS length = " + dp[m][n] + ". +" + earned);
                feedback.setForeground(ThemeManager.ACCENT_GREEN);
                award("DP_LCS", score, (int)((System.currentTimeMillis()-startTime)/1000));
            } else {
                feedback.setText(correct + "/" + total + " correct. +" + earned);
                feedback.setForeground(ThemeManager.ACCENT_PINK);
            }
            scoreLabel.setText("Score: " + score);
        }

        private void fillAnswer() {
            int m = s1.length(), n = s2.length();
            for (int i = 0; i <= m; i++)
                for (int j = 0; j <= n; j++) {
                    cells[i][j].setText(String.valueOf(dp[i][j]));
                    cells[i][j].setBackground(new Color(50,46,120));
                }
            tablePanel.repaint();
            feedback.setText("LCS length = " + dp[m][n]);
            feedback.setForeground(ThemeManager.ACCENT_YELLOW);
        }
    }

    // ─── shared helpers ───────────────────────────────────────────────────────
    static JPanel theoryCard(String heading, String html) {
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

    static JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        return l;
    }
}
