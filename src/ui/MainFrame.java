package ui;

import games.DPGamePanel;
import games.GreedyGamePanel;
import managers.SessionManager;
import ui.components.RoundedButton;
import ui.LoginPanel;
import ui.RegisterPanel;
import utils.ThemeManager;

import visualizers.DivideConquerPanel;
import visualizers.PathfindingVisualizerPanel;
import visualizers.SortingVisualizerPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Root application frame.
 * Uses a CardLayout to switch between Login, Register, and the main app shell
 * (sidebar + content area with its own CardLayout).
 */
public class MainFrame extends JFrame {

    // ── Card names ────────────────────────────────────────────────────────────
    private static final String CARD_LOGIN    = "LOGIN";
    private static final String CARD_REGISTER = "REGISTER";
    private static final String CARD_APP      = "APP";

    // ── Root ──────────────────────────────────────────────────────────────────
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel     rootPanel  = new JPanel(rootLayout);

    // ── App shell ─────────────────────────────────────────────────────────────
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel     contentPanel  = new JPanel(contentLayout);
    private SidebarPanel     sidebar;
    private DashboardPanel   dashboardPanel;
    private LeaderboardPanel leaderboardPanel;

    public MainFrame() {
        super("AlgoVerse — Algorithm Visualizer & Learning Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(new Dimension(1280, 760));
        setLocationRelativeTo(null);

        // App icon text (Unicode)
        setIconImage(createTextIcon("⚡"));

        buildRootPanel();
        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);

        // Auto-login if remember-me token found
        if (SessionManager.getInstance().isLoggedIn()) {
            showApp();
        } else {
            rootLayout.show(rootPanel, CARD_LOGIN);
        }
    }

    // ── Build panels ──────────────────────────────────────────────────────────

    private void buildRootPanel() {
        rootPanel.setBackground(ThemeManager.BG_PRIMARY);

        // Login
        LoginPanel loginPanel = new LoginPanel(
            this::showApp,
            () -> rootLayout.show(rootPanel, CARD_REGISTER)
        );

        // Register
        RegisterPanel registerPanel = new RegisterPanel(
            () -> rootLayout.show(rootPanel, CARD_LOGIN),
            () -> rootLayout.show(rootPanel, CARD_LOGIN)
        );

        // Main app shell
        JPanel appShell = buildAppShell();

        rootPanel.add(loginPanel,    CARD_LOGIN);
        rootPanel.add(registerPanel, CARD_REGISTER);
        rootPanel.add(appShell,      CARD_APP);
    }

    private JPanel buildAppShell() {
        // ── Sidebar ────────────────────────────────────────────────────────
        sidebar = new SidebarPanel(panel -> navigate(panel));

        // ── Content panels ─────────────────────────────────────────────────
        contentPanel.setBackground(ThemeManager.BG_PRIMARY);

        dashboardPanel   = new DashboardPanel(panel -> navigate(panel));
        leaderboardPanel = new LeaderboardPanel();

        contentPanel.add(dashboardPanel,                 "DASHBOARD");
        contentPanel.add(new SortingVisualizerPanel(),   "SORTING");
        contentPanel.add(new PathfindingVisualizerPanel(),"PATHFINDING");
        contentPanel.add(new DivideConquerPanel(),       "DIVIDE");
        contentPanel.add(new GreedyGamePanel(),          "GREEDY");
        contentPanel.add(new DPGamePanel(),              "DP");
        contentPanel.add(leaderboardPanel,               "LEADERBOARD");

        // ── Shell ──────────────────────────────────────────────────────────
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(ThemeManager.BG_PRIMARY);
        shell.add(sidebar,      BorderLayout.WEST);
        shell.add(contentPanel, BorderLayout.CENTER);
        return shell;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigate(String panel) {
        if ("LOGIN".equals(panel)) {
            rootLayout.show(rootPanel, CARD_LOGIN);
            return;
        }
        contentLayout.show(contentPanel, panel);
        sidebar.setActivePanel(panel);

        // Refresh data-dependent panels
        if ("DASHBOARD".equals(panel))   dashboardPanel.refresh();
        if ("LEADERBOARD".equals(panel)) leaderboardPanel.refresh();
    }

    private void showApp() {
        rootLayout.show(rootPanel, CARD_APP);
        sidebar.refreshUser();
        dashboardPanel.refresh();
    }

    // ── Icon helper ───────────────────────────────────────────────────────────

    private Image createTextIcon(String text) {
        int size = 32;
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(size, size,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.ACCENT);
        g2.fillRoundRect(0, 0, size, size, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (size - fm.stringWidth(text)) / 2,
                (size + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
        return img;
    }
}
