package ui;

public interface AlgorithmModule {
    /**
     * Returns an array of algorithm names supported by this module.
     */
    String[] getAlgorithms();

    /**
     * Called when an algorithm is selected in the global UI (e.g., Sidebar).
     * @param algorithm the name of the selected algorithm.
     */
    void onAlgorithmSelected(String algorithm);
}
