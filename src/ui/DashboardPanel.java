package ui;

import managers.SessionManager;
import managers.UserManager;
import models.User;
import ui.components.*;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main Dashboard: XP card grid, progress bar, quick-start buttons, mini leaderboard.
 */
public class DashboardPanel extends JPanel {

    public interface QuickStartListener { void onStart(String panel); }

    private QuickStartListener listener;

    // Updatable widgets
    private StatsCard xpCard, levelCard, streakCard, completedCard;
    private AnimatedProgressBar xpBar;
    private JLabel xpBarLabel;
    private JPanel leaderMini;

    public DashboardPanel(QuickStartListener listener) {
        this.listener = listener;
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel date = new JLabel(java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        date.setFont(ThemeManager.FONT_SMALL);
        date.setForeground(ThemeManager.TEXT_MUTED);

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        // ── Stats Cards ────────────────────────────────────────────────────
        xpCard        = new StatsCard("⚡", "Total XP",        "0",  ThemeManager.ACCENT);
        levelCard     = new StatsCard("🎖", "Level",           "1",  ThemeManager.ACCENT_YELLOW);
        streakCard    = new StatsCard("🔥", "Day Streak",      "0",  ThemeManager.ACCENT_ORANGE);
        completedCard = new StatsCard("✅", "Algorithms Done", "0",  ThemeManager.ACCENT_GREEN);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.add(xpCard);
        cardsPanel.add(levelCard);
        cardsPanel.add(streakCard);
        cardsPanel.add(completedCard);

        // ── XP Progress bar ────────────────────────────────────────────────
        RoundedPanel xpSection = new RoundedPanel(ThemeManager.BG_CARD, 14);
        xpSection.withBorder(ThemeManager.BORDER);
        xpSection.setLayout(new BorderLayout(0, 10));
        xpSection.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel xpTitle = new JLabel("XP Progress to Next Level");
        xpTitle.setFont(ThemeManager.FONT_MEDIUM);
        xpTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        xpBarLabel = new JLabel("0 / 500 XP");
        xpBarLabel.setFont(ThemeManager.FONT_SMALL);
        xpBarLabel.setForeground(ThemeManager.TEXT_SECONDARY);

        JPanel xpHeader = new JPanel(new BorderLayout());
        xpHeader.setOpaque(false);
        xpHeader.add(xpTitle, BorderLayout.WEST);
        xpHeader.add(xpBarLabel, BorderLayout.EAST);

        xpBar = new AnimatedProgressBar();
        xpBar.setPreferredSize(new Dimension(0, 22));
        xpBar.setFillColors(ThemeManager.ACCENT, ThemeManager.ACCENT_CYAN);

        xpSection.add(xpHeader, BorderLayout.NORTH);
        xpSection.add(xpBar, BorderLayout.CENTER);

        // ── Quick Start ────────────────────────────────────────────────────
        RoundedPanel quickPanel = new RoundedPanel(ThemeManager.BG_CARD, 14);
        quickPanel.withBorder(ThemeManager.BORDER);
        quickPanel.setLayout(new BorderLayout(0, 12));
        quickPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel qlTitle = new JLabel("Quick Start");
        qlTitle.setFont(ThemeManager.FONT_MEDIUM);
        qlTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel btnRow = new JPanel(new GridLayout(1, 5, 10, 0));
        btnRow.setOpaque(false);

        String[][] modules = {
            {"📊 Sorting",     "SORTING"},
            {"🗺 Pathfinding", "PATHFINDING"},
            {"🔀 Divide",      "DIVIDE"},
            {"🎮 Greedy",      "GREEDY"},
            {"🧩 DP Game",     "DP"},
        };
        for (String[] m : modules) {
            RoundedButton btn = new RoundedButton(m[0], RoundedButton.Style.SECONDARY);
            btn.setFont(ThemeManager.FONT_SMALL);
            String panel = m[1];
            btn.addActionListener(e -> listener.onStart(panel));
            btnRow.add(btn);
        }

        quickPanel.add(qlTitle, BorderLayout.NORTH);
        quickPanel.add(btnRow, BorderLayout.CENTER);

        // ── Mini Leaderboard ───────────────────────────────────────────────
        RoundedPanel lbPanel = new RoundedPanel(ThemeManager.BG_CARD, 14);
        lbPanel.withBorder(ThemeManager.BORDER);
        lbPanel.setLayout(new BorderLayout(0, 8));
        lbPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lbTitle = new JLabel("🏆 Top Players");
        lbTitle.setFont(ThemeManager.FONT_MEDIUM);
        lbTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        leaderMini = new JPanel();
        leaderMini.setOpaque(false);
        leaderMini.setLayout(new BoxLayout(leaderMini, BoxLayout.Y_AXIS));

        lbPanel.add(lbTitle, BorderLayout.NORTH);
        lbPanel.add(leaderMini, BorderLayout.CENTER);

        // ── Achievements strip ─────────────────────────────────────────────
        RoundedPanel achPanel = buildAchievementsPanel();

        // ── Layout assembly ────────────────────────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout(14, 0));
        topRow.setOpaque(false);
        topRow.add(xpSection, BorderLayout.CENTER);

        JPanel midRow = new JPanel(new BorderLayout(14, 0));
        midRow.setOpaque(false);
        midRow.add(quickPanel, BorderLayout.CENTER);
        midRow.add(lbPanel, BorderLayout.EAST);
        lbPanel.setPreferredSize(new Dimension(260, 0));

        JPanel centerStack = new JPanel();
        centerStack.setOpaque(false);
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.add(cardsPanel);
        centerStack.add(Box.createVerticalStrut(16));
        centerStack.add(topRow);
        centerStack.add(Box.createVerticalStrut(16));
        centerStack.add(midRow);
        centerStack.add(Box.createVerticalStrut(16));
        centerStack.add(achPanel);

        JScrollPane scroll = new JScrollPane(centerStack);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(Box.createVerticalStrut(20), BorderLayout.PAGE_START);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Achievements Panel ────────────────────────────────────────────────────

    private RoundedPanel buildAchievementsPanel() {
        RoundedPanel p = new RoundedPanel(ThemeManager.BG_CARD, 14);
        p.withBorder(ThemeManager.BORDER);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("🎖 Achievements");
        title.setFont(ThemeManager.FONT_MEDIUM);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        badges.setOpaque(false);

        String[][] ach = {
            {"🥇", "First Login", "true"},
            {"⚡", "XP Earner", "false"},
            {"🔥", "3-Day Streak", "false"},
            {"📊", "Sort Master", "false"},
            {"🗺", "Path Finder", "false"},
            {"🏆", "Top 3", "false"},
        };

        for (String[] a : ach) {
            JPanel badge = new JPanel(new BorderLayout(4, 2));
            badge.setOpaque(false);
            JLabel icon = new JLabel(a[0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            icon.setForeground(a[2].equals("true") ? ThemeManager.ACCENT_YELLOW : ThemeManager.TEXT_MUTED);
            JLabel name = new JLabel(a[1]);
            name.setFont(ThemeManager.FONT_SMALL);
            name.setForeground(a[2].equals("true") ? ThemeManager.TEXT_PRIMARY : ThemeManager.TEXT_MUTED);
            badge.add(icon, BorderLayout.NORTH);
            badge.add(name, BorderLayout.SOUTH);
            badges.add(badge);
        }

        p.add(title, BorderLayout.NORTH);
        p.add(badges, BorderLayout.CENTER);
        return p;
    }

    // ── Public refresh ────────────────────────────────────────────────────────

    /** Call whenever the user's data changes (login, XP award, etc.). */
    public void refresh() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u == null) return;

        xpCard.setValue(String.valueOf(u.getXp()));
        levelCard.setValue(String.valueOf(u.getLevel()));
        streakCard.setValue(u.getStreak() + " days");
        completedCard.setValue(String.valueOf(u.getCompletedAlgorithms().size()));

        xpBar.setProgress(u.getLevelProgress());
        xpBarLabel.setText(u.getXpInCurrentLevel() + " / " + u.getXpForNextLevel() + " XP");

        // Mini leaderboard
        leaderMini.removeAll();
        List<User> top = UserManager.getInstance().getLeaderboard();
        int rank = 1;
        for (User lu : top.subList(0, Math.min(5, top.size()))) {
            String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : rank + ".";
            boolean isMe = lu.getId().equals(u.getId());
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

            JLabel rankLbl = new JLabel(medal);
            rankLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

            JLabel nameLbl = new JLabel(lu.getUsername() + (isMe ? "  (you)" : ""));
            nameLbl.setFont(ThemeManager.FONT_SMALL);
            nameLbl.setForeground(isMe ? ThemeManager.ACCENT : ThemeManager.TEXT_PRIMARY);

            JLabel xpLbl = new JLabel(lu.getXp() + " XP");
            xpLbl.setFont(ThemeManager.FONT_SMALL);
            xpLbl.setForeground(ThemeManager.TEXT_SECONDARY);

            row.add(rankLbl, BorderLayout.WEST);
            row.add(nameLbl, BorderLayout.CENTER);
            row.add(xpLbl, BorderLayout.EAST);
            leaderMini.add(row);
            rank++;
        }
        leaderMini.revalidate();
        leaderMini.repaint();
    }
}
