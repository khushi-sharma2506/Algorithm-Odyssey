package visualizers;

import managers.SessionManager;
import managers.UserManager;
import ui.components.RoundedButton;
import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Sorting Visualizer: Bubble, Selection, Insertion, Merge, Quick, Heap Sort.
 * Step-based animation using javax.swing.Timer.
 */
public class SortingVisualizerPanel extends JPanel {

    // ── Colours for bar states ────────────────────────────────────────────────
    private static final Color COL_DEFAULT  = new Color(0x6C63FF);
    private static final Color COL_COMPARE  = new Color(0xFFD166);
    private static final Color COL_SWAP     = new Color(0xFF6584);
    private static final Color COL_SORTED   = new Color(0x43E97B);
    private static final Color COL_PIVOT    = new Color(0xFF9A3C);
    private static final Color COL_CURRENT  = new Color(0x38F9D7);

    // ── Step representation ───────────────────────────────────────────────────
    private record Step(int[] arr, int[] compare, int[] swap, int[] sorted,
                        int pivot, String msg) {}

    // ── State ─────────────────────────────────────────────────────────────────
    private int[]        array;
    private List<Step>   steps = new ArrayList<>();
    private int          stepIndex = 0;
    private boolean      paused    = false;
    private javax.swing.Timer timer;
    private int          comparisons = 0, swaps = 0;

    // ── UI ────────────────────────────────────────────────────────────────────
    private JPanel       canvas;
    private JComboBox<String> algoBox;
    private JSlider      speedSlider;
    private JLabel       statusLabel, compLabel, swapLabel;
    private JTextField   inputField;
    private RoundedButton startBtn, pauseBtn, resetBtn, stepBtn;

    public SortingVisualizerPanel() {
        setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCanvas(),  BorderLayout.CENTER);
        add(buildControls(),BorderLayout.SOUTH);

        generateRandomArray(40);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UI construction
    // ──────────────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("📊 Sorting Visualizer");
        title.setFont(ThemeManager.FONT_LARGE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        String[] algos = {"Bubble Sort","Selection Sort","Insertion Sort",
                          "Merge Sort","Quick Sort","Heap Sort"};
        algoBox = new JComboBox<>(algos);
        algoBox.setFont(ThemeManager.FONT_NORMAL);
        algoBox.setBackground(ThemeManager.BG_SURFACE);
        algoBox.setForeground(ThemeManager.TEXT_PRIMARY);
        algoBox.setPreferredSize(new Dimension(180, 34));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("Algorithm:") {{ setForeground(ThemeManager.TEXT_SECONDARY); setFont(ThemeManager.FONT_SMALL); }});
        right.add(algoBox);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JPanel buildCanvas() {
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBars((Graphics2D) g);
            }
        };
        canvas.setBackground(ThemeManager.BG_SECONDARY);
        canvas.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER, 1));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(canvas, BorderLayout.CENTER);

        // Stats row below canvas
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        stats.setOpaque(false);
        statusLabel = label("Ready");
        compLabel   = label("Comparisons: 0");
        swapLabel   = label("Swaps: 0");
        stats.add(statusLabel);
        stats.add(compLabel);
        stats.add(swapLabel);
        wrap.add(stats, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // Row 1: buttons + speed
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        startBtn = new RoundedButton("▶ Start");
        pauseBtn = new RoundedButton("⏸ Pause", RoundedButton.Style.SECONDARY);
        resetBtn = new RoundedButton("↺ Reset",  RoundedButton.Style.SECONDARY);
        stepBtn  = new RoundedButton("▸ Step",   RoundedButton.Style.GHOST);

        startBtn.addActionListener(e -> startSort());
        pauseBtn.addActionListener(e -> togglePause());
        resetBtn.addActionListener(e -> resetSort());
        stepBtn.addActionListener(e -> doStep());

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(ThemeManager.TEXT_SECONDARY);
        speedLbl.setFont(ThemeManager.FONT_SMALL);
        speedSlider = new JSlider(1, 200, 60);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(160, 28));

        btnRow.add(startBtn); btnRow.add(pauseBtn);
        btnRow.add(resetBtn); btnRow.add(stepBtn);
        btnRow.add(Box.createHorizontalStrut(16));
        btnRow.add(speedLbl); btnRow.add(speedSlider);

        // Row 2: manual input
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputRow.setOpaque(false);

        inputField = new JTextField("", 30);
        inputField.setFont(ThemeManager.FONT_MONO);
        inputField.setBackground(ThemeManager.BG_SURFACE);
        inputField.setForeground(ThemeManager.TEXT_PRIMARY);
        inputField.setCaretColor(ThemeManager.ACCENT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        inputField.setToolTipText("Enter comma-separated numbers, e.g. 5,3,8,1,9");

        RoundedButton setBtn    = new RoundedButton("Set Array",   RoundedButton.Style.SECONDARY);
        RoundedButton randBtn   = new RoundedButton("Randomise",   RoundedButton.Style.SECONDARY);
        setBtn.addActionListener(e  -> parseInputArray());
        randBtn.addActionListener(e -> { generateRandomArray(40); resetSort(); });

        JLabel hint = new JLabel("Custom input:");
        hint.setForeground(ThemeManager.TEXT_SECONDARY);
        hint.setFont(ThemeManager.FONT_SMALL);
        inputRow.add(hint); inputRow.add(inputField);
        inputRow.add(setBtn); inputRow.add(randBtn);

        p.add(btnRow,   BorderLayout.NORTH);
        p.add(inputRow, BorderLayout.SOUTH);
        return p;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Drawing
    // ──────────────────────────────────────────────────────────────────────────

    private void drawBars(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (array == null || array.length == 0) return;

        Step cur = stepIndex > 0 && stepIndex <= steps.size()
                   ? steps.get(stepIndex - 1) : null;
        int[] disp  = cur != null ? cur.arr()     : array;
        int[] comp  = cur != null ? cur.compare()  : new int[0];
        int[] swpd  = cur != null ? cur.swap()     : new int[0];
        int[] sortd = cur != null ? cur.sorted()   : new int[0];
        int   pivot = cur != null ? cur.pivot()    : -1;

        Set<Integer> compSet  = setOf(comp);
        Set<Integer> swapSet  = setOf(swpd);
        Set<Integer> sortSet  = setOf(sortd);

        int cw = canvas.getWidth(),  ch = canvas.getHeight();
        int n  = disp.length;
        int max = Arrays.stream(disp).max().orElse(1);
        float bw = (float)(cw - 4) / n;

        for (int i = 0; i < n; i++) {
            Color c;
            if (i == pivot)          c = COL_PIVOT;
            else if (swapSet.contains(i))   c = COL_SWAP;
            else if (compSet.contains(i))   c = COL_COMPARE;
            else if (sortSet.contains(i))   c = COL_SORTED;
            else                            c = COL_DEFAULT;

            int bh = (int)((double) disp[i] / max * (ch - 30));
            int bx = (int)(i * bw) + 2;
            int by = ch - bh - 2;

            g2.setPaint(new GradientPaint(bx, by, c.brighter(), bx, ch, c.darker()));
            g2.fillRoundRect(bx, by, Math.max(1, (int)bw - 1), bh, 4, 4);

            // Value label for small arrays
            if (n <= 25 && bh > 18) {
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeManager.FONT_SMALL.deriveFont(10f));
                String val = String.valueOf(disp[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, bx + ((int)bw - fm.stringWidth(val)) / 2, ch - 4);
            }
        }
    }

    private static Set<Integer> setOf(int[] arr) {
        Set<Integer> s = new HashSet<>();
        if (arr != null) for (int v : arr) s.add(v);
        return s;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sort step generators
    // ──────────────────────────────────────────────────────────────────────────

    private void generateSteps() {
        steps.clear();
        int[] arr = array.clone();
        String algo = (String) algoBox.getSelectedItem();
        assert algo != null;
        switch (algo) {
            case "Bubble Sort"    -> bubbleSort(arr);
            case "Selection Sort" -> selectionSort(arr);
            case "Insertion Sort" -> insertionSort(arr);
            case "Merge Sort"     -> mergeSortSteps(arr, 0, arr.length - 1, new TreeSet<>());
            case "Quick Sort"     -> quickSortSteps(arr, 0, arr.length - 1, new TreeSet<>());
            case "Heap Sort"      -> heapSort(arr);
        }
        // Mark all sorted at end
        int[] all = new int[arr.length];
        for (int i = 0; i < all.length; i++) all[i] = i;
        steps.add(new Step(arr, new int[0], new int[0], all, -1, "Sorted!"));
    }

    private void addStep(int[] arr, int[] cmp, int[] swp, Set<Integer> sorted, int pivot, String msg) {
        int[] s = sorted.stream().mapToInt(Integer::intValue).toArray();
        steps.add(new Step(arr.clone(), cmp, swp, s, pivot, msg));
    }

    private void bubbleSort(int[] a) {
        int n = a.length;
        Set<Integer> sorted = new TreeSet<>();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                addStep(a, new int[]{j, j+1}, new int[0], sorted, -1, "Comparing " + a[j] + " & " + a[j+1]);
                if (a[j] > a[j+1]) {
                    int t = a[j]; a[j] = a[j+1]; a[j+1] = t;
                    addStep(a, new int[0], new int[]{j, j+1}, sorted, -1, "Swapped!");
                }
            }
            sorted.add(n - 1 - i);
        }
        sorted.add(0);
    }

    private void selectionSort(int[] a) {
        int n = a.length;
        Set<Integer> sorted = new TreeSet<>();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                addStep(a, new int[]{j, min}, new int[0], sorted, i, "Finding min…");
                if (a[j] < a[min]) min = j;
            }
            if (min != i) {
                int t = a[i]; a[i] = a[min]; a[min] = t;
                addStep(a, new int[0], new int[]{i, min}, sorted, -1, "Placed min");
            }
            sorted.add(i);
        }
        sorted.add(n - 1);
    }

    private void insertionSort(int[] a) {
        int n = a.length;
        Set<Integer> sorted = new TreeSet<>();
        sorted.add(0);
        for (int i = 1; i < n; i++) {
            int key = a[i], j = i - 1;
            addStep(a, new int[]{i}, new int[0], sorted, -1, "Inserting " + key);
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                addStep(a, new int[]{j}, new int[]{j+1}, sorted, -1, "Shifting " + a[j]);
                j--;
            }
            a[j + 1] = key;
            sorted.add(i);
        }
    }

    private void mergeSortSteps(int[] a, int l, int r, Set<Integer> sorted) {
        if (l >= r) { sorted.add(l); return; }
        int m = (l + r) / 2;
        addStep(a, new int[]{l, r}, new int[0], sorted, m, "Split [" + l + ".." + r + "]");
        mergeSortSteps(a, l, m, sorted);
        mergeSortSteps(a, m+1, r, sorted);
        merge(a, l, m, r, sorted);
    }

    private void merge(int[] a, int l, int m, int r, Set<Integer> sorted) {
        int[] left  = Arrays.copyOfRange(a, l, m + 1);
        int[] right = Arrays.copyOfRange(a, m + 1, r + 1);
        int i = 0, j = 0, k = l;
        while (i < left.length && j < right.length) {
            addStep(a, new int[]{l+i, m+1+j}, new int[0], sorted, -1, "Merging");
            if (left[i] <= right[j]) a[k++] = left[i++];
            else                     a[k++] = right[j++];
            addStep(a, new int[0], new int[]{k-1}, sorted, -1, "Placed");
        }
        while (i < left.length)  { a[k++] = left[i++];  addStep(a, new int[0], new int[]{k-1}, sorted, -1, "Copy left"); }
        while (j < right.length) { a[k++] = right[j++]; addStep(a, new int[0], new int[]{k-1}, sorted, -1, "Copy right"); }
        for (int x = l; x <= r; x++) sorted.add(x);
    }

    private void quickSortSteps(int[] a, int l, int r, Set<Integer> sorted) {
        if (l >= r) { if (l == r) sorted.add(l); return; }
        int p = partition(a, l, r, sorted);
        sorted.add(p);
        quickSortSteps(a, l, p - 1, sorted);
        quickSortSteps(a, p + 1, r, sorted);
    }

    private int partition(int[] a, int l, int r, Set<Integer> sorted) {
        int pivot = a[r], i = l - 1;
        for (int j = l; j < r; j++) {
            addStep(a, new int[]{j, r}, new int[0], sorted, r, "Compare with pivot " + pivot);
            if (a[j] <= pivot) {
                i++;
                int t = a[i]; a[i] = a[j]; a[j] = t;
                addStep(a, new int[0], new int[]{i, j}, sorted, r, "Swap");
            }
        }
        int t = a[i+1]; a[i+1] = a[r]; a[r] = t;
        addStep(a, new int[0], new int[]{i+1, r}, sorted, -1, "Pivot placed");
        return i + 1;
    }

    private void heapSort(int[] a) {
        int n = a.length;
        Set<Integer> sorted = new TreeSet<>();
        for (int i = n/2 - 1; i >= 0; i--) heapify(a, n, i, sorted);
        for (int i = n - 1; i > 0; i--) {
            int t = a[0]; a[0] = a[i]; a[i] = t;
            sorted.add(i);
            addStep(a, new int[0], new int[]{0, i}, sorted, -1, "Extract max");
            heapify(a, i, 0, sorted);
        }
        sorted.add(0);
    }

    private void heapify(int[] a, int n, int i, Set<Integer> sorted) {
        int largest = i, l = 2*i+1, r = 2*i+2;
        if (l < n && a[l] > a[largest]) largest = l;
        if (r < n && a[r] > a[largest]) largest = r;
        if (largest != i) {
            addStep(a, new int[]{i, largest}, new int[0], sorted, i, "Heapify");
            int t = a[i]; a[i] = a[largest]; a[largest] = t;
            addStep(a, new int[0], new int[]{i, largest}, sorted, -1, "Swap");
            heapify(a, n, largest, sorted);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Control logic
    // ──────────────────────────────────────────────────────────────────────────

    private void startSort() {
        if (timer != null && timer.isRunning()) return;
        if (steps.isEmpty()) generateSteps();
        comparisons = 0; swaps = 0;
        paused = false;
        pauseBtn.setText("⏸ Pause");

        timer = new javax.swing.Timer(getDelay(), e -> {
            if (!paused) doStep();
        });
        timer.start();
        statusLabel.setText("Running…");
    }

    private void doStep() {
        if (stepIndex >= steps.size()) {
            if (timer != null) timer.stop();
            statusLabel.setText("✅ Sorted!");
            awardXP();
            return;
        }
        Step s = steps.get(stepIndex++);
        statusLabel.setText(s.msg());
        if (s.swap().length > 0) swaps++;
        if (s.compare().length > 0) comparisons++;
        compLabel.setText("Comparisons: " + comparisons);
        swapLabel.setText("Swaps: " + swaps);
        if (timer != null) timer.setDelay(getDelay());
        canvas.repaint();
    }

    private void togglePause() {
        paused = !paused;
        pauseBtn.setText(paused ? "▶ Resume" : "⏸ Pause");
    }

    private void resetSort() {
        if (timer != null) timer.stop();
        steps.clear();
        stepIndex = 0;
        comparisons = 0; swaps = 0;
        paused = false;
        pauseBtn.setText("⏸ Pause");
        statusLabel.setText("Ready");
        compLabel.setText("Comparisons: 0");
        swapLabel.setText("Swaps: 0");
        canvas.repaint();
    }

    private int getDelay() {
        return Math.max(5, 210 - speedSlider.getValue());
    }

    private void generateRandomArray(int size) {
        array = new int[size];
        Random rnd = new Random();
        for (int i = 0; i < size; i++) array[i] = rnd.nextInt(95) + 5;
    }

    private void parseInputArray() {
        try {
            String[] parts = inputField.getText().split(",");
            int[] a = new int[parts.length];
            for (int i = 0; i < parts.length; i++) a[i] = Integer.parseInt(parts[i].trim());
            array = a;
            resetSort();
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid input! Use comma-separated integers.");
        }
    }

    private void awardXP() {
        var sess = SessionManager.getInstance();
        if (sess.isLoggedIn()) {
            UserManager.getInstance().completeAlgorithm(
                    sess.getCurrentUser(),
                    "SORT_" + algoBox.getSelectedItem().toString().replace(" ","_").toUpperCase(),
                    50);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        return l;
    }
}
