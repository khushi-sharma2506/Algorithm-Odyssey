package ui.components;

import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A side panel to display details about an algorithm: Description, Complexities, and Stats.
 */
public class AlgorithmInfoPanel extends JPanel {

    private JLabel titleLabel;
    private JTextArea descriptionArea;
    private JLabel timeBestLabel, timeAvgLabel, timeWorstLabel, spaceLabel;
    private JLabel computeTimeLabel;

    private static class AlgoDetails {
        String description;
        String timeBest;
        String timeAvg;
        String timeWorst;
        String space;

        AlgoDetails(String desc, String tb, String ta, String tw, String sp) {
            this.description = desc;
            this.timeBest = tb;
            this.timeAvg = ta;
            this.timeWorst = tw;
            this.space = sp;
        }
    }

    private static final Map<String, AlgoDetails> DICTIONARY = new HashMap<>();

    static {
        // Sorting
        DICTIONARY.put("Bubble Sort", new AlgoDetails(
                "Repeatedly steps through the list, compares adjacent elements and swaps them if they are in the wrong order.",
                "O(N)", "O(N²)", "O(N²)", "O(1)"));
        DICTIONARY.put("Selection Sort", new AlgoDetails(
                "Divides the input list into two parts: a sorted sublist and an unsorted sublist. Repeatedly selects the smallest element from the unsorted sublist.",
                "O(N²)", "O(N²)", "O(N²)", "O(1)"));
        DICTIONARY.put("Insertion Sort", new AlgoDetails(
                "Builds the final sorted array one item at a time. It is much less efficient on large lists than more advanced algorithms.",
                "O(N)", "O(N²)", "O(N²)", "O(1)"));
        DICTIONARY.put("Merge Sort", new AlgoDetails(
                "Divide and conquer algorithm that divides the input array into two halves, calls itself for the two halves, and then merges the two sorted halves.",
                "O(N log N)", "O(N log N)", "O(N log N)", "O(N)"));
        DICTIONARY.put("Quick Sort", new AlgoDetails(
                "Picks an element as a pivot and partitions the given array around the picked pivot by placing the pivot in its correct position.",
                "O(N log N)", "O(N log N)", "O(N²)", "O(log N)"));
        DICTIONARY.put("Heap Sort", new AlgoDetails(
                "Comparison-based sorting technique based on Binary Heap data structure.",
                "O(N log N)", "O(N log N)", "O(N log N)", "O(1)"));

        // Pathfinding
        DICTIONARY.put("BFS", new AlgoDetails(
                "Breadth-First Search explores the neighbor nodes first, before moving to the next level neighbors. Guarantees the shortest path on unweighted graphs.",
                "O(V + E)", "O(V + E)", "O(V + E)", "O(V)"));
        DICTIONARY.put("DFS", new AlgoDetails(
                "Depth-First Search goes as far as it can down a given path, then backtracks until it finds an unexplored path. Does NOT guarantee shortest path.",
                "O(V + E)", "O(V + E)", "O(V + E)", "O(V)"));
        DICTIONARY.put("Dijkstra", new AlgoDetails(
                "Finds the shortest paths between nodes in a graph. It uses a priority queue to greedily select the closest node. Guarantees the shortest path.",
                "O((V+E) log V)", "O((V+E) log V)", "O((V+E) log V)", "O(V)"));
        DICTIONARY.put("A*", new AlgoDetails(
                "Uses heuristics to guide its search towards the target, guaranteeing the shortest path much faster than Dijkstra.",
                "O(E)", "O(E)", "O(E)", "O(V)"));

        // Divide & Conquer
        DICTIONARY.put("Tower of Hanoi", new AlgoDetails(
                "A mathematical puzzle where the objective is to move a stack of disks from one peg to another, following the rules: 1) Move one disk at a time, 2) Never place a larger disk on a smaller disk.",
                "O(2^N)", "O(2^N)", "O(2^N)", "O(N)"));
        DICTIONARY.put("Binary Search", new AlgoDetails(
                "Efficiently finds an item from a sorted list of items. It repeatedly divides in half the portion of the list that could contain the item, until you've narrowed the possible locations to just one.",
                "O(1)", "O(log N)", "O(log N)", "O(1)"));
        DICTIONARY.put("Merge Sort Tree", new AlgoDetails(
                "Visualises the recursive 'divide' phase of the Merge Sort algorithm, showing how an array is repeatedly halved until it cannot be divided further.",
                "O(N log N)", "O(N log N)", "O(N log N)", "O(N log N)"));
    }

    public AlgorithmInfoPanel() {
        setPreferredSize(new Dimension(280, 0));
        setBackground(ThemeManager.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, ThemeManager.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        setLayout(new BorderLayout(0, 16));

        // Title
        titleLabel = new JLabel("Algorithm Details");
        titleLabel.setFont(ThemeManager.FONT_MEDIUM);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        // Description
        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(ThemeManager.FONT_SMALL);
        descriptionArea.setForeground(ThemeManager.TEXT_SECONDARY);
        descriptionArea.setText("Select an algorithm to view details.");

        // Complexities
        JPanel compPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        compPanel.setOpaque(false);
        compPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER), "Complexities",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                ThemeManager.FONT_SMALL, ThemeManager.TEXT_PRIMARY
        ));

        timeBestLabel = createStatLabel("Best Time: ", "-");
        timeAvgLabel = createStatLabel("Avg Time: ", "-");
        timeWorstLabel = createStatLabel("Worst Time: ", "-");
        spaceLabel = createStatLabel("Space: ", "-");

        compPanel.add(timeBestLabel);
        compPanel.add(timeAvgLabel);
        compPanel.add(timeWorstLabel);
        compPanel.add(spaceLabel);

        // Live Stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 1, 0, 8));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER), "Efficiency Stats",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                ThemeManager.FONT_SMALL, ThemeManager.TEXT_PRIMARY
        ));

        computeTimeLabel = createStatLabel("Compute Time: ", "0 ms");
        statsPanel.add(computeTimeLabel);

        // Assemble
        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(titleLabel, BorderLayout.NORTH);
        topContainer.add(descriptionArea, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setOpaque(false);
        centerContainer.add(compPanel, BorderLayout.NORTH);
        centerContainer.add(statsPanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(title + value);
        label.setFont(ThemeManager.FONT_SMALL);
        label.setForeground(ThemeManager.TEXT_SECONDARY);
        return label;
    }

    private void updateStatLabel(JLabel label, String title, String value) {
        label.setText("<html><font color='" + hex(ThemeManager.TEXT_MUTED) + "'>" + title + "</font> <font color='" + hex(ThemeManager.TEXT_PRIMARY) + "'><b>" + value + "</b></font></html>");
    }

    private String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public void updateInfo(String algorithmName) {
        titleLabel.setText(algorithmName);
        AlgoDetails details = DICTIONARY.get(algorithmName);
        if (details != null) {
            descriptionArea.setText(details.description);
            updateStatLabel(timeBestLabel, "Best Time: ", details.timeBest);
            updateStatLabel(timeAvgLabel, "Avg Time: ", details.timeAvg);
            updateStatLabel(timeWorstLabel, "Worst Time: ", details.timeWorst);
            updateStatLabel(spaceLabel, "Space: ", details.space);
        } else {
            descriptionArea.setText("No details available.");
            updateStatLabel(timeBestLabel, "Best Time: ", "-");
            updateStatLabel(timeAvgLabel, "Avg Time: ", "-");
            updateStatLabel(timeWorstLabel, "Worst Time: ", "-");
            updateStatLabel(spaceLabel, "Space: ", "-");
        }
        setComputeTime(0); // Reset compute time on change
    }

    public void setComputeTime(long nanoSeconds) {
        if (nanoSeconds == 0) {
            updateStatLabel(computeTimeLabel, "Compute Time: ", "-");
            return;
        }
        double ms = nanoSeconds / 1_000_000.0;
        if (ms < 0.01) {
            updateStatLabel(computeTimeLabel, "Compute Time: ", "< 0.01 ms");
        } else {
            updateStatLabel(computeTimeLabel, "Compute Time: ", String.format("%.2f ms", ms));
        }
    }
}
