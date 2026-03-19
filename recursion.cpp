// recursion.cpp
// Algorithm Odyssey - Module 2: Recursion
// Team: The Arcane Coders | DAA-IV-T089

#include <iostream>
#include <vector>
#include <chrono>
#include <iomanip>
#include <cmath>
#include <string>

using namespace std;
using namespace chrono;

long long callCount = 0;
long long moveCount = 0;
int       maxDepth  = 0;

void resetCounters() {
    callCount = 0;
    moveCount = 0;
    maxDepth  = 0;
}

void printIndent(int depth) {
    for (int i = 0; i < depth; i++) cout << "  |  ";
}

void hanoiStep(int disk, char from, char to, char aux, bool verbose, int depth) {
    callCount++;
    if (depth > maxDepth) maxDepth = depth;

    if (disk == 1) {
        moveCount++;
        if (verbose) {
            printIndent(depth);
            cout << "Move disk 1: " << from << " -> " << to
                 << "  (move #" << moveCount << ")\n";
        }
        return;
    }

    hanoiStep(disk-1, from, aux, to, verbose, depth+1);

    moveCount++;
    if (verbose) {
        printIndent(depth);
        cout << "Move disk " << disk << ": " << from << " -> " << to
             << "  (move #" << moveCount << ")\n";
    }

    hanoiStep(disk-1, aux, to, from, verbose, depth+1);
}

void towerOfHanoi(int n, bool verbose) {
    resetCounters();
    cout << "\n=== Tower of Hanoi ===\n";
    cout << "Disks   : " << n << "\n";
    cout << "Optimal : 2^" << n << " - 1 = " << (int)(pow(2,n)-1) << " moves\n\n";

    auto start = high_resolution_clock::now();
    hanoiStep(n, 'A', 'C', 'B', verbose, 0);
    auto end   = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    cout << "\n--- Results ---\n";
    cout << "  Moves     : " << moveCount << " (optimal = " << (int)(pow(2,n)-1) << ")\n";
    cout << "  Calls     : " << callCount << "\n";
    cout << "  Max depth : " << maxDepth << "\n";
    cout << "  Time      : " << ns << " ns (" << fixed << setprecision(4) << ns/1e6 << " ms)\n";
    cout << "  Complexity: O(2^n) time, O(n) space\n";
}

long long fibCalls = 0;

long long fibRecursive(int n, bool verbose, int depth = 0) {
    fibCalls++;
    if (verbose && n <= 6) {
        printIndent(depth);
        cout << "fib(" << n << ") called  [call #" << fibCalls << "]\n";
    }
    if (n <= 1) return n;
    long long left  = fibRecursive(n-1, verbose, depth+1);
    long long right = fibRecursive(n-2, verbose, depth+1);
    if (verbose && n <= 6) {
        printIndent(depth);
        cout << "fib(" << n << ") = " << left << " + " << right << " = " << left+right << "\n";
    }
    return left + right;
}

long long fibIterative(int n) {
    if (n <= 1) return n;
    long long a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        long long c = a + b;
        a = b; b = c;
    }
    return b;
}

void fibonacci(int n, bool verbose) {
    cout << "\n=== Fibonacci (n = " << n << ") ===\n\n";

    fibCalls = 0;
    if (verbose && n <= 10) cout << "Recursive tree:\n";
    auto s1 = high_resolution_clock::now();
    long long resRec = fibRecursive(n, verbose && n <= 10);
    auto e1 = high_resolution_clock::now();
    long long nsRec = duration_cast<nanoseconds>(e1-s1).count();
    long long recCalls = fibCalls;

    auto s2 = high_resolution_clock::now();
    fibIterative(n);
    auto e2 = high_resolution_clock::now();
    long long nsIter = duration_cast<nanoseconds>(e2-s2).count();

    cout << "\nResult: " << resRec << "\n\n";
    cout << left << setw(16) << "Method"
         << setw(14) << "Time (ns)"
         << setw(12) << "Time (ms)"
         << setw(10) << "Calls" << "\n";
    cout << string(52, '-') << "\n";
    cout << left << setw(16) << "Recursive"
         << setw(14) << nsRec
         << setw(12) << fixed << setprecision(4) << nsRec/1e6
         << setw(10) << recCalls << "\n";
    cout << left << setw(16) << "Iterative"
         << setw(14) << nsIter
         << setw(12) << fixed << setprecision(4) << nsIter/1e6
         << setw(10) << 1 << "\n";

    cout << "\nRecursive: O(2^n) time  — " << recCalls << " calls for n=" << n << "\n";
    cout << "Iterative: O(n) time    — 1 loop pass\n";
}

long long factCalls = 0;

long long factRecursive(int n, bool verbose, int depth = 0) {
    factCalls++;
    if (verbose) {
        printIndent(depth);
        cout << "factorial(" << n << ") called\n";
    }
    if (n <= 1) {
        if (verbose) { printIndent(depth); cout << "Base case -> return 1\n"; }
        return 1;
    }
    long long result = n * factRecursive(n-1, verbose, depth+1);
    if (verbose) {
        printIndent(depth);
        cout << "factorial(" << n << ") = " << n << " * " << n-1 << "! = " << result << "\n";
    }
    return result;
}

void factorial(int n, bool verbose) {
    factCalls = 0;
    cout << "\n=== Factorial (n = " << n << ") ===\n\n";

    auto start = high_resolution_clock::now();
    long long result = factRecursive(n, verbose);
    auto end   = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    cout << "\n" << n << "! = " << result << "\n";
    cout << "Calls : " << factCalls << "\n";
    cout << "Time  : " << ns << " ns\n";
    cout << "Complexity: O(n) time, O(n) space\n";
}

long long bsCalls = 0;

int binarySearchRecursive(const vector<int>& arr, int low, int high,
                           int target, bool verbose, int depth = 0) {
    bsCalls++;
    if (verbose) {
        printIndent(depth);
        cout << "Search [" << low << ".." << high << "]"
             << "  mid=" << (low+high)/2
             << "  arr[mid]=" << arr[(low+high)/2] << "\n";
    }
    if (low > high) {
        if (verbose) { printIndent(depth); cout << "Not found\n"; }
        return -1;
    }
    int mid = (low + high) / 2;
    if (arr[mid] == target) {
        if (verbose) { printIndent(depth); cout << "Found " << target << " at index " << mid << "\n"; }
        return mid;
    }
    if (arr[mid] < target)
        return binarySearchRecursive(arr, mid+1, high, target, verbose, depth+1);
    return binarySearchRecursive(arr, low, mid-1, target, verbose, depth+1);
}

void binarySearch(int size, int target, bool verbose) {
    bsCalls = 0;
    vector<int> arr;
    for (int i = 0; i < size; i++) arr.push_back(i * 2 + 1);

    cout << "\n=== Binary Search ===\n";
    cout << "Array size : " << size << "\n";
    cout << "Target     : " << target << "\n";
    if (verbose) {
        cout << "Array: [ ";
        for (int x : arr) cout << x << " ";
        cout << "]\n\n";
    }

    auto start = high_resolution_clock::now();
    int result = binarySearchRecursive(arr, 0, size-1, target, verbose);
    auto end   = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    cout << "\nResult : " << (result == -1 ? "Not found" : "Found at index " + to_string(result)) << "\n";
    cout << "Calls  : " << bsCalls << "  (log2(" << size << ") = " << (int)log2(size) << ")\n";
    cout << "Time   : " << ns << " ns\n";
    cout << "Complexity: O(log n)\n";
}

void compareHanoi() {
    cout << "\n=== Hanoi - Disk Count Comparison ===\n\n";
    cout << left << setw(8) << "Disks"
         << setw(14) << "Moves"
         << setw(16) << "Time (ns)"
         << setw(12) << "Time (ms)"
         << setw(10) << "Calls" << "\n";
    cout << string(60, '-') << "\n";

    for (int n = 1; n <= 20; n++) {
        resetCounters();
        auto start = high_resolution_clock::now();
        hanoiStep(n, 'A', 'C', 'B', false, 0);
        auto end   = high_resolution_clock::now();
        long long ns = duration_cast<nanoseconds>(end - start).count();

        cout << left << setw(8) << n
             << setw(14) << moveCount
             << setw(16) << ns
             << setw(12) << fixed << setprecision(4) << ns/1e6
             << setw(10) << callCount << "\n";
    }
    cout << "\nEach disk added doubles the moves -> O(2^n)\n";
}

int main() {
    cout << "\nAlgorithm Odyssey - Recursion\n";
    cout << "The Arcane Coders | DAA-IV-T089\n\n";

    cout << "Select:\n";
    cout << "1. Tower of Hanoi\n";
    cout << "2. Fibonacci (recursive vs iterative)\n";
    cout << "3. Factorial\n";
    cout << "4. Binary Search\n";
    cout << "5. Hanoi disk count comparison (1-20)\n";
    cout << "\nEnter choice (1-5): ";

    int choice;
    cin >> choice;

    if (choice == 1) {
        cout << "Number of disks (2-10 for step view, up to 25 for timing): ";
        int n; cin >> n;
        towerOfHanoi(n, n <= 8);

    } else if (choice == 2) {
        cout << "Enter n (1-40): ";
        int n; cin >> n;
        fibonacci(n, n <= 10);

    } else if (choice == 3) {
        cout << "Enter n (1-15): ";
        int n; cin >> n;
        factorial(n, true);

    } else if (choice == 4) {
        cout << "Array size: ";
        int sz; cin >> sz;
        cout << "Target to search: ";
        int t; cin >> t;
        binarySearch(sz, t, sz <= 32);

    } else if (choice == 5) {
        compareHanoi();
    }

    return 0;
}
