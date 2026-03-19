// dc.cpp
// Algorithm Odyssey - Module 3: Divide & Conquer
// Team: The Arcane Coders | DAA-IV-T089

#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <iomanip>
#include <cstdlib>
#include <cmath>
#include <string>

using namespace std;
using namespace chrono;

long long comparisons = 0;
long long mergeOps    = 0;
long long callCount   = 0;
int       maxDepth    = 0;

void resetCounters() {
    comparisons = 0;
    mergeOps    = 0;
    callCount   = 0;
    maxDepth    = 0;
}

void printArray(const vector<int>& arr, const string& label = "") {
    if (!label.empty()) cout << label << ": ";
    cout << "[ ";
    for (int x : arr) cout << x << " ";
    cout << "]\n";
}

void printIndent(int depth) {
    for (int i = 0; i < depth; i++) cout << "  |  ";
}

vector<int> generateInput(const string& type, int size) {
    vector<int> arr;
    srand(42);
    if (type == "random")
        for (int i = 0; i < size; i++) arr.push_back(rand() % 100 + 1);
    else if (type == "sorted")
        for (int i = 1; i <= size; i++) arr.push_back(i);
    else if (type == "reverse")
        for (int i = size; i >= 1; i--) arr.push_back(i);
    else if (type == "nearly") {
        for (int i = 1; i <= size; i++) arr.push_back(i);
        for (int i = 0; i < size/10; i++) {
            int a = rand() % size, b = rand() % size;
            swap(arr[a], arr[b]);
        }
    } else if (type == "large")
        for (int i = 0; i < size; i++) arr.push_back(rand() % 10000 + 1000);
    else if (type == "small")
        for (int i = 0; i < size; i++) arr.push_back(rand() % 10 + 1);
    return arr;
}

void mergeHalves(vector<int>& arr, int left, int mid, int right,
                 bool verbose, int depth) {
    vector<int> L(arr.begin() + left,    arr.begin() + mid + 1);
    vector<int> R(arr.begin() + mid + 1, arr.begin() + right + 1);

    if (verbose) {
        printIndent(depth);
        cout << "Merge  left=[ ";
        for (int x : L) cout << x << " ";
        cout << "]  right=[ ";
        for (int x : R) cout << x << " ";
        cout << "]\n";
    }

    int i = 0, j = 0, k = left;
    while (i < (int)L.size() && j < (int)R.size()) {
        comparisons++;
        if (L[i] <= R[j]) arr[k++] = L[i++];
        else               arr[k++] = R[j++];
        mergeOps++;
    }
    while (i < (int)L.size()) { arr[k++] = L[i++]; mergeOps++; }
    while (j < (int)R.size()) { arr[k++] = R[j++]; mergeOps++; }

    if (verbose) {
        printIndent(depth);
        cout << "Result [ ";
        for (int x = left; x <= right; x++) cout << arr[x] << " ";
        cout << "]\n";
    }
}

void mergeSortHelper(vector<int>& arr, int left, int right,
                     bool verbose, int depth) {
    callCount++;
    if (depth > maxDepth) maxDepth = depth;

    if (left >= right) {
        if (verbose) {
            printIndent(depth);
            cout << "Base [" << arr[left] << "]\n";
        }
        return;
    }

    int mid = (left + right) / 2;

    if (verbose) {
        printIndent(depth);
        cout << "Divide [" << left << ".." << right << "]"
             << " -> left[" << left << ".." << mid << "]"
             << " right[" << mid+1 << ".." << right << "]\n";
    }

    mergeSortHelper(arr, left,  mid,   verbose, depth+1);
    mergeSortHelper(arr, mid+1, right, verbose, depth+1);
    mergeHalves(arr, left, mid, right, verbose, depth);
}

void mergeSort(vector<int> arr, bool verbose) {
    resetCounters();
    cout << "\n=== Merge Sort ===\n";
    if (verbose) printArray(arr, "Input");
    cout << "\n";

    auto start = high_resolution_clock::now();
    mergeSortHelper(arr, 0, (int)arr.size()-1, verbose, 0);
    auto end   = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");

    cout << "\n--- Results ---\n";
    cout << "  Time       : " << ns << " ns (" << fixed << setprecision(4) << ns/1e6 << " ms)\n";
    cout << "  Comparisons: " << comparisons << "\n";
    cout << "  Merge ops  : " << mergeOps << "\n";
    cout << "  Calls      : " << callCount << "\n";
    cout << "  Max depth  : " << maxDepth << "\n";
    cout << "  Complexity : O(n log n) — same for all input types\n";
}

int partitionArr(vector<int>& arr, int low, int high,
                 bool verbose, int depth) {
    int pivot = arr[high];
    int i = low - 1;

    if (verbose) {
        printIndent(depth);
        cout << "Pivot=" << pivot << " range[" << low << ".." << high << "]\n";
    }

    for (int j = low; j < high; j++) {
        comparisons++;
        if (arr[j] <= pivot) {
            i++;
            swap(arr[i], arr[j]);
        }
    }
    swap(arr[i+1], arr[high]);
    int pivotPos = i + 1;

    if (verbose) {
        printIndent(depth);
        cout << "Pivot " << pivot << " placed at index " << pivotPos << "  -> [ ";
        for (int k = low; k <= high; k++) {
            if (k == pivotPos) cout << "*" << arr[k] << "* ";
            else               cout << arr[k] << " ";
        }
        cout << "]\n";
    }
    return pivotPos;
}

void quickSortHelper(vector<int>& arr, int low, int high,
                     bool verbose, int depth) {
    callCount++;
    if (depth > maxDepth) maxDepth = depth;
    if (low >= high) return;

    int pi = partitionArr(arr, low, high, verbose, depth);

    if (verbose) {
        printIndent(depth);
        cout << "Recurse left[" << low << ".." << pi-1
             << "]  right[" << pi+1 << ".." << high << "]\n";
    }

    quickSortHelper(arr, low,   pi-1, verbose, depth+1);
    quickSortHelper(arr, pi+1,  high, verbose, depth+1);
}

void quickSort(vector<int> arr, bool verbose) {
    resetCounters();
    cout << "\n=== Quick Sort ===\n";
    if (verbose) printArray(arr, "Input");
    cout << "\n";

    auto start = high_resolution_clock::now();
    quickSortHelper(arr, 0, (int)arr.size()-1, verbose, 0);
    auto end   = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");

    cout << "\n--- Results ---\n";
    cout << "  Time       : " << ns << " ns (" << fixed << setprecision(4) << ns/1e6 << " ms)\n";
    cout << "  Comparisons: " << comparisons << "\n";
    cout << "  Calls      : " << callCount << "\n";
    cout << "  Max depth  : " << maxDepth << "\n";
    cout << "  Complexity : O(n log n) avg, O(n^2) worst case\n";
}

struct Point { int x, y; };

long long crossProduct(Point A, Point B, Point P) {
    return (long long)(B.x-A.x)*(P.y-A.y) - (long long)(B.y-A.y)*(P.x-A.x);
}

long long distFromLine(Point A, Point B, Point P) {
    return abs((long long)(B.x-A.x)*(A.y-P.y) - (long long)(A.x-P.x)*(B.y-A.y));
}

vector<Point> hullPoints;

void hullRecurse(vector<Point>& pts, Point A, Point B,
                 bool verbose, int depth) {
    callCount++;

    vector<Point> leftSide;
    for (auto& P : pts) {
        if (crossProduct(A, B, P) > 0)
            leftSide.push_back(P);
    }

    if (leftSide.empty()) {
        if (verbose) {
            printIndent(depth);
            cout << "Hull point (" << B.x << "," << B.y << ") confirmed\n";
        }
        hullPoints.push_back(B);
        return;
    }

    Point farthest = leftSide[0];
    long long maxDist = 0;
    for (auto& P : leftSide) {
        long long d = distFromLine(A, B, P);
        if (d > maxDist) { maxDist = d; farthest = P; }
    }

    if (verbose) {
        printIndent(depth);
        cout << "Line (" << A.x << "," << A.y << ")->(" << B.x << "," << B.y << ")"
             << "  farthest=(" << farthest.x << "," << farthest.y << ")\n";
    }

    hullRecurse(pts, A,        farthest, verbose, depth+1);
    hullRecurse(pts, farthest, B,        verbose, depth+1);
}

void convexHull(int numPoints, bool verbose) {
    vector<Point> pts;
    srand(42);
    for (int i = 0; i < numPoints; i++)
        pts.push_back({rand() % 100, rand() % 100});

    cout << "\n=== Convex Hull ===\n";
    cout << numPoints << " points generated\n";
    if (verbose) {
        for (auto& p : pts)
            cout << "  (" << p.x << ", " << p.y << ")\n";
    }

    Point leftmost = pts[0], rightmost = pts[0];
    for (auto& p : pts) {
        if (p.x < leftmost.x)  leftmost  = p;
        if (p.x > rightmost.x) rightmost = p;
    }

    if (verbose) {
        cout << "\nLeftmost : (" << leftmost.x  << "," << leftmost.y  << ")\n";
        cout << "Rightmost: (" << rightmost.x << "," << rightmost.y << ")\n\n";
    }

    hullPoints.clear();
    callCount = 0;

    auto start = high_resolution_clock::now();
    hullPoints.push_back(leftmost);
    hullRecurse(pts, leftmost,  rightmost, verbose, 1);
    hullRecurse(pts, rightmost, leftmost,  verbose, 1);
    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    sort(hullPoints.begin(), hullPoints.end(),
         [](const Point& a, const Point& b){ return a.x < b.x; });
    hullPoints.erase(unique(hullPoints.begin(), hullPoints.end(),
         [](const Point& a, const Point& b){ return a.x==b.x && a.y==b.y; }),
         hullPoints.end());

    cout << "\nHull boundary (" << hullPoints.size() << " of " << numPoints << " points):\n";
    for (auto& p : hullPoints)
        cout << "  (" << p.x << ", " << p.y << ")\n";

    cout << "\nTime  : " << ns << " ns\n";
    cout << "Calls : " << callCount << "\n";
    cout << "Complexity: O(n log n) average\n";
}

int kthSmallestHelper(vector<int> arr, int k, bool verbose, int depth = 0) {
    callCount++;
    if (depth > maxDepth) maxDepth = depth;

    int n = arr.size();

    if (verbose) {
        printIndent(depth);
        cout << "Size=" << n << " finding k=" << k << "\n";
    }

    if (n == 1) {
        if (verbose) { printIndent(depth); cout << "Found: " << arr[0] << "\n"; }
        return arr[0];
    }

    int mid = n / 2;
    vector<int> left(arr.begin(), arr.begin() + mid);
    vector<int> right(arr.begin() + mid, arr.end());

    vector<int> sortedLeft = left;
    sort(sortedLeft.begin(), sortedLeft.end());
    int leftMax = sortedLeft.back();

    int countSmall = 0;
    for (int x : arr) {
        comparisons++;
        if (x <= leftMax) countSmall++;
    }

    if (verbose) {
        printIndent(depth);
        cout << countSmall << " elements <= " << leftMax << "\n";
    }

    if (k <= countSmall) {
        if (verbose) { printIndent(depth); cout << "k=" << k << " -> go left\n"; }
        return kthSmallestHelper(left, k, verbose, depth+1);
    } else {
        if (verbose) { printIndent(depth); cout << "k=" << k << " -> go right\n"; }
        return kthSmallestHelper(right, k - countSmall, verbose, depth+1);
    }
}

void kthSmallest(int size, bool verbose) {
    resetCounters();
    vector<int> arr = generateInput("random", size);
    int k = (size + 1) / 2;

    cout << "\n=== Kth Smallest (finding median) ===\n";
    printArray(arr, "Input");
    cout << "Finding k=" << k << "\n\n";

    auto start  = high_resolution_clock::now();
    int result  = kthSmallestHelper(arr, k, verbose);
    auto end    = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    vector<int> check = arr;
    sort(check.begin(), check.end());

    cout << "\nAnswer : " << result << "\n";
    cout << "Verify : sorted[" << k-1 << "] = " << check[k-1]
         << (result == check[k-1] ? "  -> correct" : "  -> recheck") << "\n";
    cout << "Calls  : " << callCount << "\n";
    cout << "Time   : " << ns << " ns\n";
    cout << "Complexity: O(n log n)\n";
}

void compareMergeVsQuick() {
    cout << "\n=== Merge Sort vs Quick Sort ===\n";
    int size = 500;
    cout << "Array size: " << size << "\n\n";

    cout << left << setw(12) << "Input"
         << setw(16) << "Merge (ns)"
         << setw(16) << "Quick (ns)"
         << setw(14) << "Merge comps"
         << setw(14) << "Quick comps" << "\n";
    cout << string(72, '-') << "\n";

    string types[] = {"random","sorted","reverse","nearly","large","small"};
    for (auto& t : types) {
        vector<int> a1 = generateInput(t, size);
        resetCounters();
        auto s1 = high_resolution_clock::now();
        mergeSortHelper(a1, 0, size-1, false, 0);
        auto e1 = high_resolution_clock::now();
        long long nsMerge = duration_cast<nanoseconds>(e1-s1).count();
        long long cMerge  = comparisons;

        vector<int> a2 = generateInput(t, size);
        resetCounters();
        auto s2 = high_resolution_clock::now();
        quickSortHelper(a2, 0, size-1, false, 0);
        auto e2 = high_resolution_clock::now();
        long long nsQuick = duration_cast<nanoseconds>(e2-s2).count();
        long long cQuick  = comparisons;

        cout << left << setw(12) << t
             << setw(16) << nsMerge
             << setw(16) << nsQuick
             << setw(14) << cMerge
             << setw(14) << cQuick << "\n";
    }

    cout << "\nMerge Sort: always O(n log n), consistent\n";
    cout << "Quick Sort: fast on random, slow on sorted (bad pivot)\n";
    cout << "Trade off : Merge needs O(n) extra space, Quick uses O(log n)\n";
}

int main() {
    cout << "\nAlgorithm Odyssey - Divide & Conquer\n";
    cout << "The Arcane Coders | DAA-IV-T089\n\n";

    cout << "Select algorithm:\n";
    cout << "1. Merge Sort\n";
    cout << "2. Quick Sort\n";
    cout << "3. Convex Hull\n";
    cout << "4. Kth Smallest / Median\n";
    cout << "5. Compare Merge Sort vs Quick Sort\n";
    cout << "\nEnter choice (1-5): ";

    int choice;
    cin >> choice;

    if (choice == 1) {
        cout << "Input type (1.Random 2.Sorted 3.Reverse 4.Nearly 5.Large 6.Small): ";
        int inp; cin >> inp;
        cout << "Array size (5-10 for step view, larger for timing): ";
        int sz; cin >> sz;
        string types[] = {"random","sorted","reverse","nearly","large","small"};
        mergeSort(generateInput(types[inp-1], sz), sz <= 10);

    } else if (choice == 2) {
        cout << "Input type (1.Random 2.Sorted 3.Reverse 4.Nearly 5.Large 6.Small): ";
        int inp; cin >> inp;
        cout << "Array size (5-10 for step view, larger for timing): ";
        int sz; cin >> sz;
        string types[] = {"random","sorted","reverse","nearly","large","small"};
        quickSort(generateInput(types[inp-1], sz), sz <= 10);

    } else if (choice == 3) {
        cout << "Number of points (5-20): ";
        int n; cin >> n;
        convexHull(n, n <= 15);

    } else if (choice == 4) {
        cout << "Array size (5-20 for step view): ";
        int sz; cin >> sz;
        kthSmallest(sz, sz <= 15);

    } else if (choice == 5) {
        compareMergeVsQuick();
    }

    return 0;
}
