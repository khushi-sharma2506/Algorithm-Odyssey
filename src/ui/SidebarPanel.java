package ui;

import managers.AuthManager;
import managers.SessionManager;
import ui.components.RoundedButton;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Left-side navigation sidebar.
 * Items: Dashboard, Sorting, Pathfinding, Divide&Conquer, Greedy Game, DP Game, Leaderboard, Logout
 */
public class SidebarPanel extends JPanel {

    public interface NavListener {
        void navigate(String panel);
    }

    private NavListener listener;
    private String activePanel = "DASHBOARD";

    private static final String[][] NAV_ITEMS = {
        {"🏠", "Dashboard",     "DASHBOARD"},
        {"📊", "Sorting",       "SORTING"},
        {"🗺", "Pathfinding",   "PATHFINDING"},
        {"🔀", "Divide & Conquer", "DIVIDE"},
        {"🎮", "Greedy Game",   "GREEDY"},
        {"🧩", "DP Game",       "DP"},
        {"🏆", "Leaderboard",   "LEADERBOARD"},
    };

    private JLabel userLabel;
    private JLabel levelLabel;
    private JButton[] navButtons;

    public SidebarPanel(NavListener listener) {
        this.listener = listener;
        setPreferredSize(new Dimension(220, 0));
        setBackground(ThemeManager.BG_SECONDARY);
        setLayout(new BorderLayout());

        // ── Logo ────────────────────────────────────────────────────────────
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 12, 20));

        JLabel logo = new JLabel("AlgoVerse");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(ThemeManager.ACCENT);

        JLabel tagline = new JLabel("Algorithm Learning");
        tagline.setFont(ThemeManager.FONT_SMALL);
        tagline.setForeground(ThemeManager.TEXT_MUTED);

        logoPanel.add(logo, BorderLayout.CENTER);
        logoPanel.add(tagline, BorderLayout.SOUTH);
        logoPanel.setPreferredSize(new Dimension(220, 72));

        // ── Nav Items ────────────────────────────────────────────────────────
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        navButtons = new JButton[NAV_ITEMS.length];
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            String[] item = NAV_ITEMS[i];
            JButton btn   = createNavButton(item[0], item[1], item[2]);
            navButtons[i] = btn;
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(4));
        }

        // ── User Card ────────────────────────────────────────────────────────
        JPanel userCard = new JPanel(new BorderLayout(8, 2));
        userCard.setOpaque(false);
        userCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));

        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        userInfo.setOpaque(false);

        userLabel = new JLabel("Guest");
        userLabel.setFont(ThemeManager.FONT_MEDIUM);
        userLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        levelLabel = new JLabel("Level 1 · 0 XP");
        levelLabel.setFont(ThemeManager.FONT_SMALL);
        levelLabel.setForeground(ThemeManager.TEXT_SECONDARY);

        userInfo.add(userLabel);
        userInfo.add(levelLabel);

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        userCard.add(avatarLabel, BorderLayout.WEST);
        userCard.add(userInfo, BorderLayout.CENTER);

        // ── Logout button ────────────────────────────────────────────────────
        RoundedButton logoutBtn = new RoundedButton("⏻  Logout", RoundedButton.Style.GHOST);
        logoutBtn.setFont(ThemeManager.FONT_NORMAL);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            AuthManager.getInstance().logout();
            listener.navigate("LOGIN");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.BORDER);

        bottomPanel.add(sep);
        bottomPanel.add(userCard);
        bottomPanel.add(Box.createVerticalStrut(4));
        JPanel logoutWrapper = new JPanel(new BorderLayout());
        logoutWrapper.setOpaque(false);
        logoutWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        logoutWrapper.add(logoutBtn);
        bottomPanel.add(logoutWrapper);

        // ── Assemble ─────────────────────────────────────────────────────────
        add(logoPanel,   BorderLayout.NORTH);
        add(new JScrollPane(navPanel) {{
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
        }}, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ── Right border line ────────────────────────────────────────────────
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.BORDER));
    }

    // ── Navigation button factory ─────────────────────────────────────────────

    private JButton createNavButton(String icon, String label, String panel) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = activePanel.equals(panel);
                if (active) {
                    g2.setColor(new Color(108, 99, 255, 40));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(ThemeManager.ACCENT);
                    g2.fillRect(0, 6, 3, getHeight() - 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(ThemeManager.FONT_NORMAL);
        btn.setForeground(activePanel.equals(panel)
                ? ThemeManager.ACCENT : ThemeManager.TEXT_SECONDARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!activePanel.equals(panel))
                    btn.setForeground(ThemeManager.TEXT_PRIMARY);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!activePanel.equals(panel))
                    btn.setForeground(ThemeManager.TEXT_SECONDARY);
            }
        });

        btn.addActionListener(e -> {
            setActivePanel(panel);
            listener.navigate(panel);
        });
        return btn;
    }

    public void setActivePanel(String panel) {
        this.activePanel = panel;
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            boolean active = NAV_ITEMS[i][2].equals(panel);
            navButtons[i].setForeground(active ? ThemeManager.ACCENT : ThemeManager.TEXT_SECONDARY);
        }
        repaint();
    }

    /** Refresh user info labels from current session. */
    public void refreshUser() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userLabel.setText(user.getUsername());
            levelLabel.setText("Level " + user.getLevel() + "  ·  " + user.getXp() + " XP");
        } else {
            userLabel.setText("Guest");
            levelLabel.setText("Level 1 · 0 XP");
        }
    }
}
