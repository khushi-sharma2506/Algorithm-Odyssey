package ui;

import managers.SessionManager;
import managers.UserManager;
import models.User;
import utils.ThemeManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Full leaderboard screen with a styled JTable sorted by XP.
 * Highlights the current user in accent colour.
 */
public class LeaderboardPanel extends JPanel {

    private JTable    table;
    private DefaultTableModel model;
    private JLabel    titleLabel;

    public LeaderboardPanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // ── Header ──────────────────────────────────────────────────────────
        titleLabel = new JLabel("🏆 Leaderboard");
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel sub = new JLabel("Ranked by total XP");
        sub.setFont(ThemeManager.FONT_SMALL);
        sub.setForeground(ThemeManager.TEXT_MUTED);

        JButton refreshBtn = styledBtn("⟳ Refresh");
        refreshBtn.addActionListener(e -> refresh());

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        JPanel hLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        hLeft.setOpaque(false);
        hLeft.add(titleLabel);
        hLeft.add(sub);
        header.add(hLeft, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);

        // ── Table ────────────────────────────────────────────────────────────
        String[] cols = {"#", "Player", "Level", "XP", "Algorithms"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                String me = SessionManager.getInstance().getCurrentUser() != null
                        ? SessionManager.getInstance().getCurrentUser().getUsername() : "";
                Object nameCell = model.getValueAt(row, 1);
                boolean isMe = nameCell != null && nameCell.toString().replace(" ★", "").equals(me);

                if (isMe) {
                    c.setBackground(new Color(108, 99, 255, 40));
                    c.setForeground(ThemeManager.ACCENT);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (row % 2 == 0) {
                    c.setBackground(ThemeManager.BG_SECONDARY);
                    c.setForeground(ThemeManager.TEXT_PRIMARY);
                } else {
                    c.setBackground(ThemeManager.BG_PRIMARY);
                    c.setForeground(ThemeManager.TEXT_PRIMARY);
                }
                return c;
            }
        };

        table.setBackground(ThemeManager.BG_SECONDARY);
        table.setForeground(ThemeManager.TEXT_PRIMARY);
        table.setGridColor(ThemeManager.BORDER);
        table.setSelectionBackground(ThemeManager.ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(40);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFont(ThemeManager.FONT_NORMAL);
        table.setFocusable(false);
        table.getTableHeader().setFont(ThemeManager.FONT_MEDIUM);
        table.getTableHeader().setBackground(ThemeManager.BG_SURFACE);
        table.getTableHeader().setForeground(ThemeManager.TEXT_SECONDARY);
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER));

        // Column widths
        int[] widths = {50, 220, 80, 100, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Centre columns except username
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 2, 3, 4}) {
            table.getColumnModel().getColumn(i).setCellRenderer(centre);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(ThemeManager.BG_SECONDARY);
        scroll.getViewport().setBackground(ThemeManager.BG_SECONDARY);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER, 1));

        // ── Medals legend ─────────────────────────────────────────────────────
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.setOpaque(false);
        for (String[] item : new String[][]{{"🥇","1st"},{"🥈","2nd"},{"🥉","3rd"},
                                             {"🟣","You"}}) {
            JLabel l = new JLabel(item[0] + " " + item[1]);
            l.setFont(ThemeManager.FONT_SMALL);
            l.setForeground(ThemeManager.TEXT_SECONDARY);
            legend.add(l);
        }

        add(header, BorderLayout.NORTH);
        add(Box.createVerticalStrut(16), BorderLayout.PAGE_START);
        add(scroll, BorderLayout.CENTER);
        add(legend, BorderLayout.SOUTH);
    }

    /** Reload data from UserManager and repopulate the table. */
    public void refresh() {
        model.setRowCount(0);
        List<User> users = UserManager.getInstance().getLeaderboard();
        String myName = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getUsername() : "";

        String[] medals = {"🥇", "🥈", "🥉"};
        int rank = 1;
        for (User u : users) {
            String rankStr = rank <= 3 ? medals[rank - 1] : String.valueOf(rank);
            String name    = u.getUsername() + (u.getUsername().equals(myName) ? " ★" : "");
            model.addRow(new Object[]{rankStr, name, "Lv." + u.getLevel(),
                                      u.getXp() + " XP", u.getCompletedAlgorithms().size()});
            rank++;
        }
        table.repaint();
    }

    private JButton styledBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(ThemeManager.BG_SURFACE);
        b.setForeground(ThemeManager.TEXT_PRIMARY);
        b.setFont(ThemeManager.FONT_NORMAL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
