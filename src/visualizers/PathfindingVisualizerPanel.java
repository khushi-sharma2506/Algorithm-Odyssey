package visualizers;

import managers.SessionManager;
import managers.UserManager;
import ui.components.RoundedButton;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Grid-based Pathfinding Visualizer.
 * Algorithms: BFS, DFS, Dijkstra, A*.
 * Click to place walls; right-click source/destination.
 */
public class PathfindingVisualizerPanel extends JPanel {

    // ── Cell states ───────────────────────────────────────────────────────────
    private static final int EMPTY   = 0;
    private static final int WALL    = 1;
    private static final int START   = 2;
    private static final int END     = 3;
    private static final int VISITED = 4;
    private static final int FRONTIER= 5;
    private static final int PATH    = 6;

    private static final Color[] CELL_COLORS = {
        ThemeManager.BG_SURFACE,               // EMPTY
        new Color(0x2A2A5A),                   // WALL
        ThemeManager.ACCENT_GREEN,             // START
        ThemeManager.ACCENT_PINK,              // END
        new Color(0x38F9D7, false),            // VISITED  -- cyan tint
        ThemeManager.ACCENT_YELLOW,            // FRONTIER
        ThemeManager.ACCENT,                   // PATH
    };

    static { CELL_COLORS[4] = new Color(56, 180, 200, 180); }

    // ── Grid ──────────────────────────────────────────────────────────────────
    private static final int ROWS = 22, COLS = 42;
    private final int[][] grid = new int[ROWS][COLS];
    private int startR = 5,  startC = 5;
    private int endR   = 16, endC   = 36;

    // ── Algorithm state ───────────────────────────────────────────────────────
    private final Queue<int[]>    frontier  = new LinkedList<>();
    private final Map<String,String> cameFrom = new HashMap<>();
    private boolean running = false, done = false;
    private javax.swing.Timer   timer;
    private int[][] dist; // for Dijkstra

    // ── UI ────────────────────────────────────────────────────────────────────
    private GridCanvas  gridCanvas;
    private JComboBox<String> algoBox;
    private JLabel      statusLabel;
    private RoundedButton startBtn, resetBtn, clearBtn;
    private int drawMode = WALL; // left-click places this cell type

    public PathfindingVisualizerPanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        initGrid();

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildGrid(),    BorderLayout.CENTER);
        add(buildControls(),BorderLayout.SOUTH);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Grid canvas
    // ──────────────────────────────────────────────────────────────────────────

    private class GridCanvas extends JPanel {
        GridCanvas() {
            setBackground(ThemeManager.BG_SECONDARY);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            MouseAdapter ma = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e)  { handleMouse(e); }
                @Override public void mouseDragged(MouseEvent e)  { handleMouse(e); }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cw = getWidth() / COLS, ch = getHeight() / ROWS;
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int state = grid[r][c];
                    int x = c * cw, y = r * ch;
                    g2.setColor(CELL_COLORS[state]);
                    g2.fillRoundRect(x+1, y+1, cw-2, ch-2, 4, 4);
                    // Labels for start/end
                    if (state == START || state == END) {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.min(cw, ch) - 4));
                        String sym = state == START ? "S" : "E";
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(sym, x + (cw - fm.stringWidth(sym))/2,
                                          y + (ch + fm.getAscent() - fm.getDescent())/2);
                    }
                }
            }
            // Grid lines
            g2.setColor(ThemeManager.BORDER);
            for (int r = 0; r <= ROWS; r++) g2.drawLine(0, r * ch, COLS * cw, r * ch);
            for (int c = 0; c <= COLS; c++) g2.drawLine(c * cw, 0, c * cw, ROWS * ch);
            g2.dispose();
        }
    }

    private void handleMouse(MouseEvent e) {
        if (running) return;
        int cw = gridCanvas.getWidth() / COLS;
        int ch = gridCanvas.getHeight() / ROWS;
        int c  = e.getX() / cw, r = e.getY() / ch;
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) return;

        if (SwingUtilities.isRightMouseButton(e)) {
            // Right click cycles: empty → wall → start → end
            grid[r][c] = (grid[r][c] + 1) % 4;
            if (grid[r][c] == START) { grid[startR][startC] = EMPTY; startR = r; startC = c; }
            if (grid[r][c] == END)   { grid[endR][endC]     = EMPTY; endR   = r; endC   = c; }
        } else {
            // Left click toggles wall
            if (grid[r][c] == EMPTY) grid[r][c] = WALL;
            else if (grid[r][c] == WALL) grid[r][c] = EMPTY;
        }
        gridCanvas.repaint();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UI construction
    // ──────────────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("🗺 Pathfinding Visualizer");
        title.setFont(ThemeManager.FONT_LARGE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        String[] algos = {"BFS", "DFS", "Dijkstra", "A*"};
        algoBox = new JComboBox<>(algos);
        algoBox.setBackground(ThemeManager.BG_SURFACE);
        algoBox.setForeground(ThemeManager.TEXT_PRIMARY);
        algoBox.setFont(ThemeManager.FONT_NORMAL);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("Algorithm:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
        right.add(algoBox);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JPanel buildGrid() {
        gridCanvas = new GridCanvas();
        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);
        wrap.add(gridCanvas, BorderLayout.CENTER);

        statusLabel = new JLabel("Click cells to draw walls. Right-click to set Start(S)/End(E).");
        statusLabel.setFont(ThemeManager.FONT_SMALL);
        statusLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        wrap.add(statusLabel, BorderLayout.SOUTH);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        legend.setOpaque(false);
        String[][] leg = {{"■", "Empty", "0"},{"■", "Wall", "1"},{"■","Start","2"},
                          {"■","End","3"},{"■","Visited","4"},{"■","Path","6"}};
        for (String[] item : leg) {
            int idx = Integer.parseInt(item[2]);
            JLabel l = new JLabel(item[0] + " " + item[1]);
            l.setFont(ThemeManager.FONT_SMALL);
            l.setForeground(CELL_COLORS[idx]);
            legend.add(l);
        }
        wrap.add(legend, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);

        startBtn = new RoundedButton("▶ Find Path");
        resetBtn = new RoundedButton("↺ Reset",    RoundedButton.Style.SECONDARY);
        clearBtn = new RoundedButton("✕ Clear",    RoundedButton.Style.SECONDARY);

        startBtn.addActionListener(e -> startSearch());
        resetBtn.addActionListener(e -> { resetSearch(); initGrid(); gridCanvas.repaint(); });
        clearBtn.addActionListener(e -> { resetSearch(); clearVisited(); gridCanvas.repaint(); });

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(ThemeManager.TEXT_SECONDARY);
        speedLbl.setFont(ThemeManager.FONT_SMALL);
        JSlider speed = new JSlider(1, 100, 30);
        speed.setOpaque(false);
        speed.setPreferredSize(new Dimension(120, 24));

        p.add(startBtn); p.add(resetBtn); p.add(clearBtn);
        p.add(Box.createHorizontalStrut(12));
        p.add(speedLbl); p.add(speed);

        // Add maze generation button
        RoundedButton mazeBtn = new RoundedButton("🎲 Random Maze", RoundedButton.Style.GHOST);
        mazeBtn.addActionListener(e -> { resetSearch(); generateMaze(); gridCanvas.repaint(); });
        p.add(mazeBtn);

        timer = new javax.swing.Timer(30, null);
        timer.addActionListener(e -> {
            int delay = Math.max(5, 105 - speed.getValue());
            timer.setDelay(delay);
            stepSearch();
        });

        return p;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Algorithm implementations
    // ──────────────────────────────────────────────────────────────────────────

    private void startSearch() {
        if (running) return;
        clearVisited();
        frontier.clear();
        cameFrom.clear();
        done = false;
        running = true;
        statusLabel.setText("Searching…");

        String algo = (String) algoBox.getSelectedItem();
        if ("Dijkstra".equals(algo) || "A*".equals(algo)) {
            initDist();
        }
        frontier.offer(new int[]{startR, startC, 0}); // {row, col, cost}
        grid[startR][startC] = START;
        timer.start();
    }

    private void stepSearch() {
        if (frontier.isEmpty() || done) {
            timer.stop();
            running = false;
            if (!done) statusLabel.setText("❌ No path found!");
            return;
        }

        String algo = (String) algoBox.getSelectedItem();
        int[] cur;

        if ("DFS".equals(algo)) {
            // Use stack behaviour (poll from front of priority queue acting as stack)
            cur = ((LinkedList<int[]>) frontier).peekLast();
            ((LinkedList<int[]>) frontier).removeLast();
        } else if ("Dijkstra".equals(algo)) {
            // Pick lowest cost
            cur = pickLowestCost();
        } else if ("A*".equals(algo)) {
            cur = pickLowestFScore();
        } else {
            cur = frontier.poll(); // BFS
        }

        if (cur == null) return;
        int r = cur[0], c = cur[1];

        if (r == endR && c == endC) {
            done = true;
            timer.stop();
            running = false;
            reconstructPath();
            statusLabel.setText("✅ Path found! Length: " + getPathLength());
            awardXP();
            return;
        }

        if (grid[r][c] != START && grid[r][c] != END)
            grid[r][c] = VISITED;

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) continue;
            if (grid[nr][nc] == WALL || grid[nr][nc] == VISITED) continue;
            String key = nr + "," + nc;
            if (!cameFrom.containsKey(key)) {
                cameFrom.put(key, r + "," + c);
                int newCost = cur[2] + 1;
                if ("Dijkstra".equals(algo) && newCost >= dist[nr][nc]) continue;
                if ("Dijkstra".equals(algo)) dist[nr][nc] = newCost;
                frontier.offer(new int[]{nr, nc, newCost});
                if (grid[nr][nc] != END) grid[nr][nc] = FRONTIER;
            }
        }
        gridCanvas.repaint();
    }

    private int[] pickLowestCost() {
        int[] best = null; int minCost = Integer.MAX_VALUE;
        for (int[] cell : frontier) {
            if (cell[2] < minCost) { minCost = cell[2]; best = cell; }
        }
        frontier.remove(best);
        return best;
    }

    private int[] pickLowestFScore() {
        int[] best = null; int minF = Integer.MAX_VALUE;
        for (int[] cell : frontier) {
            int f = cell[2] + heuristic(cell[0], cell[1]);
            if (f < minF) { minF = f; best = cell; }
        }
        frontier.remove(best);
        return best;
    }

    private int heuristic(int r, int c) {
        return Math.abs(r - endR) + Math.abs(c - endC);
    }

    private void reconstructPath() {
        String key = endR + "," + endC;
        while (cameFrom.containsKey(key)) {
            String[] parts = key.split(",");
            int r = Integer.parseInt(parts[0]), c = Integer.parseInt(parts[1]);
            if (grid[r][c] != START && grid[r][c] != END) grid[r][c] = PATH;
            key = cameFrom.get(key);
        }
        gridCanvas.repaint();
    }

    private int getPathLength() {
        int len = 0;
        String key = endR + "," + endC;
        while (cameFrom.containsKey(key)) { key = cameFrom.get(key); len++; }
        return len;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private void initGrid() {
        for (int[] row : grid) Arrays.fill(row, EMPTY);
        grid[startR][startC] = START;
        grid[endR][endC]     = END;
    }

    private void clearVisited() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (grid[r][c] == VISITED || grid[r][c] == FRONTIER || grid[r][c] == PATH)
                    grid[r][c] = EMPTY;
    }

    private void resetSearch() {
        if (timer != null) timer.stop();
        running = false; done = false;
        frontier.clear(); cameFrom.clear();
        statusLabel.setText("Click cells to draw walls. Right-click to set Start(S)/End(E).");
    }

    private void initDist() {
        dist = new int[ROWS][COLS];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[startR][startC] = 0;
    }

    private void generateMaze() {
        Random rnd = new Random();
        initGrid();
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (grid[r][c] == EMPTY && rnd.nextFloat() < 0.3f)
                    grid[r][c] = WALL;
        grid[startR][startC] = START;
        grid[endR][endC]     = END;
    }

    private void awardXP() {
        var sess = SessionManager.getInstance();
        if (sess.isLoggedIn()) {
            UserManager.getInstance().completeAlgorithm(
                    sess.getCurrentUser(),
                    "PATH_" + algoBox.getSelectedItem().toString().toUpperCase(),
                    60);
        }
    }
}
