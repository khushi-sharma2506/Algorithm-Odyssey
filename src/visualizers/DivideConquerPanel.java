package visualizers;

import managers.SessionManager;
import managers.UserManager;
import ui.components.RoundedButton;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Divide & Conquer Visualizer.
 * Tabs: Tower of Hanoi | Binary Search | Merge Sort Tree
 */
public class DivideConquerPanel extends JPanel {

    public DivideConquerPanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("🔀 Divide & Conquer");
        title.setFont(ThemeManager.FONT_LARGE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeManager.BG_SECONDARY);
        tabs.setForeground(ThemeManager.TEXT_PRIMARY);
        tabs.setFont(ThemeManager.FONT_NORMAL);

        tabs.addTab("🗼 Tower of Hanoi", new HanoiPanel());
        tabs.addTab("🔍 Binary Search",  new BinarySearchPanel());
        tabs.addTab("🔀 Merge Sort Tree",new MergeSortTreePanel());

        add(title, BorderLayout.NORTH);
        add(tabs,  BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Tower of Hanoi
    // ══════════════════════════════════════════════════════════════════════════

    static class HanoiPanel extends JPanel {
        private final List<List<Integer>> pegs = new ArrayList<>();
        private final List<int[]> moves = new ArrayList<>();
        private int step = 0;
        private int diskCount = 5;
        private Timer timer;

        HanoiPanel() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 10));
            setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            JPanel canvas = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawHanoi((Graphics2D) g);
                }
            };
            canvas.setBackground(ThemeManager.BG_SECONDARY);
            canvas.setPreferredSize(new Dimension(0, 280));
            canvas.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));

            JLabel info = new JLabel("Moves: 0");
            info.setForeground(ThemeManager.TEXT_SECONDARY);
            info.setFont(ThemeManager.FONT_SMALL);

            JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            ctrl.setOpaque(false);

            JSpinner diskSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 8, 1));
            diskSpinner.setFont(ThemeManager.FONT_NORMAL);

            RoundedButton startBtn = new RoundedButton("▶ Animate");
            RoundedButton resetBtn = new RoundedButton("↺ Reset", RoundedButton.Style.SECONDARY);

            JSlider speed = new JSlider(1, 20, 5);
            speed.setOpaque(false);

            startBtn.addActionListener(e -> {
                diskCount = (int) diskSpinner.getValue();
                initPegs();
                moves.clear(); step = 0;
                generateHanoi(diskCount, 0, 2, 1);
                timer = new Timer(1100 / speed.getValue(), null);
                timer.addActionListener(te -> {
                    timer.setDelay(1100 / speed.getValue());
                    if (step < moves.size()) {
                        int[] m = moves.get(step++);
                        int disk = pegs.get(m[0]).remove(pegs.get(m[0]).size()-1);
                        pegs.get(m[1]).add(disk);
                        info.setText("Move " + step + " / " + moves.size());
                        canvas.repaint();
                    } else {
                        timer.stop();
                        info.setText("✅ Done in " + moves.size() + " moves!");
                    }
                });
                timer.start();
            });

            resetBtn.addActionListener(e -> {
                if (timer != null) timer.stop();
                diskCount = (int) diskSpinner.getValue();
                initPegs(); moves.clear(); step = 0;
                info.setText("Moves: 0");
                canvas.repaint();
            });

            ctrl.add(new JLabel("Disks:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
            ctrl.add(diskSpinner);
            ctrl.add(startBtn); ctrl.add(resetBtn);
            ctrl.add(new JLabel("Speed:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
            ctrl.add(speed);

            initPegs();
            add(canvas, BorderLayout.CENTER);
            add(ctrl,   BorderLayout.SOUTH);
            add(info,   BorderLayout.NORTH);
        }

        private void initPegs() {
            pegs.clear();
            for (int i = 0; i < 3; i++) pegs.add(new ArrayList<>());
            for (int d = diskCount; d >= 1; d--) pegs.get(0).add(d);
        }

        private void generateHanoi(int n, int from, int to, int aux) {
            if (n == 0) return;
            generateHanoi(n - 1, from, aux, to);
            moves.add(new int[]{from, to});
            generateHanoi(n - 1, aux, to, from);
        }

        private void drawHanoi(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() > 0 ? getWidth() : 600;
            int h = 280;
            int pegW = 8, pegH = h - 40;
            int baseY = h - 20;
            int[] pegX = {w/6, w/2, 5*w/6};
            Color[] diskColors = {ThemeManager.ACCENT, ThemeManager.ACCENT_PINK,
                                  ThemeManager.ACCENT_GREEN, ThemeManager.ACCENT_YELLOW,
                                  ThemeManager.ACCENT_CYAN, ThemeManager.ACCENT_ORANGE,
                                  new Color(0xBF84FF), new Color(0xFF4757)};

            // Base line
            g2.setColor(ThemeManager.TEXT_MUTED);
            g2.fillRoundRect(20, baseY - 6, w - 40, 10, 6, 6);

            // Pegs
            String[] labels = {"A", "B", "C"};
            for (int i = 0; i < 3; i++) {
                g2.setColor(ThemeManager.TEXT_SECONDARY);
                g2.fillRoundRect(pegX[i] - pegW/2, baseY - pegH, pegW, pegH, 4, 4);
                g2.setFont(ThemeManager.FONT_MEDIUM);
                g2.setColor(ThemeManager.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(labels[i], pegX[i] - fm.stringWidth(labels[i])/2, baseY + 16);
            }

            // Disks
            int maxDiskW = w / 4;
            for (int p = 0; p < 3; p++) {
                List<Integer> peg = pegs.get(p);
                for (int d = 0; d < peg.size(); d++) {
                    int size  = peg.get(d);
                    int dw    = (int)(maxDiskW * size / (diskCount + 1.0)) + 30;
                    int dh    = 18;
                    int dx    = pegX[p] - dw / 2;
                    int dy    = baseY - dh * (d + 1) - 6;
                    Color col = diskColors[(size - 1) % diskColors.length];
                    g2.setPaint(new GradientPaint(dx, dy, col.brighter(), dx, dy + dh, col.darker()));
                    g2.fillRoundRect(dx, dy, dw, dh, 8, 8);
                    g2.setColor(ThemeManager.TEXT_PRIMARY);
                    g2.setFont(ThemeManager.FONT_SMALL.deriveFont(11f));
                    FontMetrics fm2 = g2.getFontMetrics();
                    String lbl = String.valueOf(size);
                    g2.drawString(lbl, pegX[p] - fm2.stringWidth(lbl)/2, dy + dh/2 + fm2.getAscent()/2 - 2);
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Binary Search
    // ══════════════════════════════════════════════════════════════════════════

    static class BinarySearchPanel extends JPanel {
        private int[] arr;
        private List<int[]> steps = new ArrayList<>(); // {left, mid, right, target}
        private int step = 0;
        private Timer timer;

        BinarySearchPanel() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 10));
            setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            JPanel canvas = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawSearch((Graphics2D) g);
                }
            };
            canvas.setBackground(ThemeManager.BG_SECONDARY);
            canvas.setPreferredSize(new Dimension(0, 200));
            canvas.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));

            JLabel status = new JLabel("Enter a target value and click Search");
            status.setFont(ThemeManager.FONT_SMALL);
            status.setForeground(ThemeManager.TEXT_SECONDARY);

            JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            ctrl.setOpaque(false);

            JTextField targetField = new JTextField("42", 6);
            targetField.setBackground(ThemeManager.BG_SURFACE);
            targetField.setForeground(ThemeManager.TEXT_PRIMARY);
            targetField.setFont(ThemeManager.FONT_NORMAL);
            targetField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.BORDER),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));

            RoundedButton searchBtn = new RoundedButton("🔍 Search");
            RoundedButton resetBtn  = new RoundedButton("↺ Reset", RoundedButton.Style.SECONDARY);

            searchBtn.addActionListener(e -> {
                if (timer != null) timer.stop();
                generateSortedArray();
                steps.clear(); step = 0;
                try {
                    int target = Integer.parseInt(targetField.getText().trim());
                    generateBinarySearchSteps(target);
                } catch (NumberFormatException ex) { status.setText("Invalid target!"); return; }
                timer = new Timer(600, null);
                timer.addActionListener(te -> {
                    if (step < steps.size()) {
                        int[] s = steps.get(step++);
                        status.setText("L=" + arr[s[0]] + " M=" + arr[s[1]] + " R=" + arr[s[2]]
                                + (step == steps.size() ?
                                  (s[1] >= 0 ? "  ✅ Found at index " + s[1] : "  ❌ Not found") : ""));
                        canvas.repaint();
                    } else { timer.stop(); }
                });
                timer.start();
            });

            resetBtn.addActionListener(e -> { if (timer != null) timer.stop(); generateSortedArray(); steps.clear(); step = 0; status.setText("Ready"); canvas.repaint(); });

            ctrl.add(new JLabel("Target:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
            ctrl.add(targetField);
            ctrl.add(searchBtn); ctrl.add(resetBtn);

            generateSortedArray();
            add(canvas, BorderLayout.CENTER);
            add(ctrl,   BorderLayout.SOUTH);
            add(status, BorderLayout.NORTH);
        }

        private void generateSortedArray() {
            arr = new int[20];
            for (int i = 0; i < 20; i++) arr[i] = i * 5 + 2;
        }

        private void generateBinarySearchSteps(int target) {
            int l = 0, r = arr.length - 1;
            while (l <= r) {
                int m = (l + r) / 2;
                steps.add(new int[]{l, m, r, target});
                if (arr[m] == target) break;
                else if (arr[m] < target) l = m + 1;
                else r = m - 1;
            }
            if (steps.isEmpty() || arr[steps.get(steps.size()-1)[1]] != target) {
                steps.add(new int[]{0, -1, arr.length-1, target});
            }
        }

        private void drawSearch(Graphics2D g2) {
            if (arr == null) return;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cw = getWidth() > 0 ? getWidth() : 600, ch = 200;
            int cellW = cw / arr.length, cellH = 50, startY = ch/2 - cellH/2;

            int[] cur = step > 0 ? steps.get(step - 1) : null;

            for (int i = 0; i < arr.length; i++) {
                Color bg = ThemeManager.BG_SURFACE;
                if (cur != null) {
                    if (i == cur[1])                          bg = ThemeManager.ACCENT_GREEN;
                    else if (i >= cur[0] && i <= cur[2])     bg = new Color(108, 99, 255, 80);
                    else                                      bg = ThemeManager.BG_SECONDARY;
                }
                g2.setColor(bg);
                g2.fillRoundRect(i * cellW + 2, startY, cellW - 4, cellH, 8, 8);
                g2.setColor(ThemeManager.TEXT_PRIMARY);
                g2.setFont(ThemeManager.FONT_SMALL.deriveFont(11f));
                FontMetrics fm = g2.getFontMetrics();
                String val = String.valueOf(arr[i]);
                g2.drawString(val, i * cellW + (cellW - fm.stringWidth(val))/2, startY + cellH/2 + fm.getAscent()/2 - 2);

                // Index label
                g2.setColor(ThemeManager.TEXT_MUTED);
                g2.setFont(ThemeManager.FONT_SMALL.deriveFont(9f));
                g2.drawString(String.valueOf(i), i * cellW + cellW/2 - 4, startY + cellH + 14);

                // L / M / R markers
                if (cur != null) {
                    g2.setFont(ThemeManager.FONT_SMALL.deriveFont(10f).deriveFont(Font.BOLD));
                    if (i == cur[0]) { g2.setColor(ThemeManager.ACCENT_CYAN);  g2.drawString("L", i * cellW + 2, startY - 4); }
                    if (i == cur[1]) { g2.setColor(ThemeManager.ACCENT_GREEN); g2.drawString("M", i * cellW + cellW/2 - 4, startY - 4); }
                    if (i == cur[2]) { g2.setColor(ThemeManager.ACCENT_PINK);  g2.drawString("R", i * cellW + cellW - 12, startY - 4); }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Merge Sort Tree (recursive call tree visualisation)
    // ══════════════════════════════════════════════════════════════════════════

    static class MergeSortTreePanel extends JPanel {
        private final List<int[][]> nodes = new ArrayList<>(); // {arr, l, r, depth}
        private int animStep = 0;
        private Timer timer;

        MergeSortTreePanel() {
            setBackground(ThemeManager.BG_PRIMARY);
            setLayout(new BorderLayout(0, 10));
            setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            JPanel canvas = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawTree((Graphics2D) g);
                }
            };
            canvas.setBackground(ThemeManager.BG_SECONDARY);
            canvas.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));

            JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            ctrl.setOpaque(false);

            JTextField arrField = new JTextField("38,27,43,3,9,82,10", 28);
            arrField.setBackground(ThemeManager.BG_SURFACE);
            arrField.setForeground(ThemeManager.TEXT_PRIMARY);
            arrField.setFont(ThemeManager.FONT_MONO);
            arrField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.BORDER),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));

            RoundedButton animBtn = new RoundedButton("▶ Visualize");
            RoundedButton resetBtn = new RoundedButton("↺ Reset", RoundedButton.Style.SECONDARY);

            animBtn.addActionListener(e -> {
                if (timer != null) timer.stop();
                nodes.clear(); animStep = 0;
                try {
                    String[] parts = arrField.getText().split(",");
                    int[] arr = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) arr[i] = Integer.parseInt(parts[i].trim());
                    collectNodes(arr, 0, arr.length - 1, 0);
                } catch (Exception ex) { return; }
                timer = new Timer(500, null);
                timer.addActionListener(te -> {
                    if (animStep < nodes.size()) { animStep++; canvas.repaint(); }
                    else timer.stop();
                });
                timer.start();
            });

            resetBtn.addActionListener(e -> { if (timer != null) timer.stop(); nodes.clear(); animStep = 0; canvas.repaint(); });

            ctrl.add(new JLabel("Array:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
            ctrl.add(arrField); ctrl.add(animBtn); ctrl.add(resetBtn);

            add(canvas, BorderLayout.CENTER);
            add(ctrl,   BorderLayout.SOUTH);
        }

        private void collectNodes(int[] arr, int l, int r, int depth) {
            nodes.add(new int[][]{arr.clone(), {l, r, depth}});
            if (l >= r) return;
            int m = (l + r) / 2;
            collectNodes(arr, l, m, depth + 1);
            collectNodes(arr, m + 1, r, depth + 1);
        }

        private void drawTree(Graphics2D g2) {
            if (nodes.isEmpty() || animStep == 0) return;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cw = getWidth() > 0 ? getWidth() : 700, ch = getHeight() > 0 ? getHeight() : 400;

            Color[] depthColors = {ThemeManager.ACCENT, ThemeManager.ACCENT_CYAN,
                                   ThemeManager.ACCENT_GREEN, ThemeManager.ACCENT_YELLOW,
                                   ThemeManager.ACCENT_ORANGE};

            for (int i = 0; i < Math.min(animStep, nodes.size()); i++) {
                int[][] node  = nodes.get(i);
                int[] arr     = node[0];
                int l = node[1][0], r = node[1][1], depth = node[1][2];

                int len   = r - l + 1;
                int cellW = Math.min(36, cw / (arr.length + 2));
                int cellH = 28;
                int totalW = len * cellW;
                int xOff  = cw / 2 - totalW / 2 + l * cellW - arr.length * cellW / 2;
                int yOff  = depth * (cellH + 28) + 20;

                Color col = depthColors[depth % depthColors.length];

                for (int k = 0; k < len; k++) {
                    int x = xOff + k * cellW;
                    g2.setPaint(new GradientPaint(x, yOff, col, x, yOff + cellH, col.darker()));
                    g2.fillRoundRect(x + 1, yOff, cellW - 2, cellH, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.setFont(ThemeManager.FONT_SMALL.deriveFont(10f));
                    FontMetrics fm = g2.getFontMetrics();
                    String v = String.valueOf(arr[l + k]);
                    g2.drawString(v, x + (cellW - fm.stringWidth(v))/2, yOff + cellH/2 + fm.getAscent()/2 - 2);
                }
            }
        }
    }
}
