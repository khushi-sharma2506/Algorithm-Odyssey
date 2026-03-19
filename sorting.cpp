// sorting.cpp
// Algorithm Odyssey - Module 1: Sorting Algorithms
// Team: The Arcane Coders | DAA-IV-T089

#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <cstdlib>
#include <iomanip>
#include <string>

using namespace std;
using namespace chrono;

long long comparisons = 0;
long long swaps       = 0;

void resetCounters() {
    comparisons = 0;
    swaps       = 0;
}

void printArray(const vector<int>& arr, const string& label = "") {
    if (!label.empty()) cout << label << ": ";
    cout << "[ ";
    for (int x : arr) cout << x << " ";
    cout << "]\n";
}

void printStep(const vector<int>& arr, int i, int j, const string& action) {
    cout << "  " << action << " [" << i << " & " << j << "] -> [ ";
    for (int k = 0; k < (int)arr.size(); k++) {
        if (k == i || k == j) cout << "*" << arr[k] << "* ";
        else cout << arr[k] << " ";
    }
    cout << "]\n";
}

void printResult(const string& name, long long ns, long long comps, long long sw) {
    cout << "\n--- " << name << " Results ---\n";
    cout << "  Time        : " << ns << " ns (" << fixed << setprecision(4) << ns/1e6 << " ms)\n";
    cout << "  Comparisons : " << comps << "\n";
    cout << "  Swaps       : " << sw << "\n";
}

vector<int> generateInput(const string& type, int size) {
    vector<int> arr;
    srand(42);

    if (type == "random") {
        for (int i = 0; i < size; i++)
            arr.push_back(rand() % 100 + 1);
    } else if (type == "sorted") {
        for (int i = 1; i <= size; i++)
            arr.push_back(i);
    } else if (type == "reverse") {
        for (int i = size; i >= 1; i--)
            arr.push_back(i);
    } else if (type == "nearly") {
        for (int i = 1; i <= size; i++)
            arr.push_back(i);
        for (int i = 0; i < size / 10; i++) {
            int a = rand() % size, b = rand() % size;
            swap(arr[a], arr[b]);
        }
    } else if (type == "large") {
        for (int i = 0; i < size; i++)
            arr.push_back(rand() % 10000 + 1000);
    } else if (type == "small") {
        for (int i = 0; i < size; i++)
            arr.push_back(rand() % 10 + 1);
    }

    return arr;
}

void bubbleSort(vector<int> arr, bool verbose) {
    resetCounters();
    int n = arr.size();
    if (verbose) {
        cout << "\n=== Bubble Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();

    for (int i = 0; i < n - 1; i++) {
        bool swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            comparisons++;
            if (arr[j] > arr[j + 1]) {
                swap(arr[j], arr[j + 1]);
                swaps++;
                swapped = true;
                if (verbose) printStep(arr, j, j + 1, "Swap");
            }
        }
        if (!swapped) break;
    }

    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");
    printResult("Bubble Sort", ns, comparisons, swaps);
}

void selectionSort(vector<int> arr, bool verbose) {
    resetCounters();
    int n = arr.size();
    if (verbose) {
        cout << "\n=== Selection Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();

    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < n; j++) {
            comparisons++;
            if (arr[j] < arr[minIdx])
                minIdx = j;
        }
        if (minIdx != i) {
            swap(arr[i], arr[minIdx]);
            swaps++;
            if (verbose) printStep(arr, i, minIdx, "MinSwap");
        }
    }

    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");
    printResult("Selection Sort", ns, comparisons, swaps);
}

void insertionSort(vector<int> arr, bool verbose) {
    resetCounters();
    int n = arr.size();
    if (verbose) {
        cout << "\n=== Insertion Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();

    for (int i = 1; i < n; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            comparisons++;
            arr[j + 1] = arr[j];
            swaps++;
            j--;
            if (verbose) printStep(arr, j + 1, i, "Shift");
        }
        comparisons++;
        arr[j + 1] = key;
    }

    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");
    printResult("Insertion Sort", ns, comparisons, swaps);
}

int partition(vector<int>& arr, int low, int high, bool verbose) {
    int pivot = arr[high];
    int i = low - 1;
    for (int j = low; j < high; j++) {
        comparisons++;
        if (arr[j] <= pivot) {
            i++;
            swap(arr[i], arr[j]);
            swaps++;
            if (verbose) printStep(arr, i, j, "Partition");
        }
    }
    swap(arr[i + 1], arr[high]);
    swaps++;
    return i + 1;
}

void quickSortHelper(vector<int>& arr, int low, int high, bool verbose) {
    if (low < high) {
        int pi = partition(arr, low, high, verbose);
        quickSortHelper(arr, low, pi - 1, verbose);
        quickSortHelper(arr, pi + 1, high, verbose);
    }
}

void quickSort(vector<int> arr, bool verbose) {
    resetCounters();
    if (verbose) {
        cout << "\n=== Quick Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();
    quickSortHelper(arr, 0, arr.size() - 1, verbose);
    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");
    printResult("Quick Sort", ns, comparisons, swaps);
}

void heapify(vector<int>& arr, int n, int i, bool verbose) {
    int largest = i, left = 2*i+1, right = 2*i+2;
    comparisons++;
    if (left < n && arr[left] > arr[largest]) largest = left;
    comparisons++;
    if (right < n && arr[right] > arr[largest]) largest = right;
    if (largest != i) {
        swap(arr[i], arr[largest]);
        swaps++;
        if (verbose) printStep(arr, i, largest, "Heapify");
        heapify(arr, n, largest, verbose);
    }
}

void heapSort(vector<int> arr, bool verbose) {
    resetCounters();
    int n = arr.size();
    if (verbose) {
        cout << "\n=== Heap Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();

    for (int i = n/2 - 1; i >= 0; i--)
        heapify(arr, n, i, verbose);
    for (int i = n - 1; i > 0; i--) {
        swap(arr[0], arr[i]);
        swaps++;
        heapify(arr, i, 0, verbose);
    }

    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(arr, "Sorted");
    printResult("Heap Sort", ns, comparisons, swaps);
}

void countingSort(vector<int> arr, bool verbose) {
    resetCounters();
    if (verbose) {
        cout << "\n=== Counting Sort ===\n";
        printArray(arr, "Input");
    }

    auto start = high_resolution_clock::now();

    int maxVal = *max_element(arr.begin(), arr.end());
    int minVal = *min_element(arr.begin(), arr.end());
    int range  = maxVal - minVal + 1;

    vector<int> count(range, 0);
    vector<int> output(arr.size());

    for (int x : arr) count[x - minVal]++;
    for (int i = 1; i < range; i++) count[i] += count[i-1];
    for (int i = arr.size()-1; i >= 0; i--) {
        output[count[arr[i]-minVal]-1] = arr[i];
        count[arr[i]-minVal]--;
        swaps++;
    }

    auto end = high_resolution_clock::now();
    long long ns = duration_cast<nanoseconds>(end - start).count();

    if (verbose) printArray(output, "Sorted");
    printResult("Counting Sort", ns, comparisons, swaps);
}

void compareAll(const string& inputType, int size) {
    cout << "\n=== Compare All - " << inputType << " input, size " << size << " ===\n";

    vector<int> base = generateInput(inputType, size);
    printArray(base, "Input");
    cout << "\n";

    cout << left << setw(18) << "Algorithm"
         << setw(14) << "Time (ns)"
         << setw(12) << "Time (ms)"
         << setw(14) << "Comparisons"
         << setw(10) << "Swaps" << "\n";
    cout << string(68, '-') << "\n";

    // Bubble
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      int n=a.size(); for(int i=0;i<n-1;i++) for(int j=0;j<n-i-1;j++){comparisons++;if(a[j]>a[j+1]){swap(a[j],a[j+1]);swaps++;}}
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Bubble Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }

    // Selection
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      int n=a.size(); for(int i=0;i<n-1;i++){int m=i;for(int j=i+1;j<n;j++){comparisons++;if(a[j]<a[m])m=j;}if(m!=i){swap(a[i],a[m]);swaps++;}}
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Selection Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }

    // Insertion
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      int n=a.size(); for(int i=1;i<n;i++){int k=a[i],j=i-1;while(j>=0&&a[j]>k){comparisons++;a[j+1]=a[j];swaps++;j--;}comparisons++;a[j+1]=k;}
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Insertion Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }

    // Quick
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      quickSortHelper(a, 0, a.size()-1, false);
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Quick Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }

    // Heap
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      int n=a.size(); for(int i=n/2-1;i>=0;i--) heapify(a,n,i,false); for(int i=n-1;i>0;i--){swap(a[0],a[i]);swaps++;heapify(a,i,0,false);}
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Heap Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }

    // Counting
    { vector<int> a=base; resetCounters();
      auto s=high_resolution_clock::now();
      int mx=*max_element(a.begin(),a.end()),mn=*min_element(a.begin(),a.end()),r=mx-mn+1;
      vector<int> c(r,0),o(a.size());
      for(int x:a)c[x-mn]++; for(int i=1;i<r;i++)c[i]+=c[i-1];
      for(int i=a.size()-1;i>=0;i--){o[c[a[i]-mn]-1]=a[i];c[a[i]-mn]--;swaps++;}
      long long ns=duration_cast<nanoseconds>(high_resolution_clock::now()-s).count();
      cout<<left<<setw(18)<<"Counting Sort"<<setw(14)<<ns<<setw(12)<<fixed<<setprecision(4)<<ns/1e6<<setw(14)<<comparisons<<setw(10)<<swaps<<"\n"; }
}

int main() {
    cout << "\nAlgorithm Odyssey - Sorting Algorithms\n";
    cout << "The Arcane Coders | DAA-IV-T089\n\n";

    cout << "Select mode:\n";
    cout << "1. Run one algorithm (step by step)\n";
    cout << "2. Compare all algorithms on same input\n";
    cout << "3. Run all input types\n";
    cout << "\nEnter choice (1/2/3): ";

    int choice;
    cin >> choice;

    if (choice == 1) {
        cout << "\nSelect algorithm:\n";
        cout << "1. Bubble Sort\n2. Selection Sort\n3. Insertion Sort\n";
        cout << "4. Quick Sort\n5. Heap Sort\n6. Counting Sort\n";
        cout << "Enter (1-6): ";
        int algo; cin >> algo;

        cout << "\nSelect input type:\n";
        cout << "1. Random\n2. Sorted\n3. Reverse\n4. Nearly Sorted\n5. Large Values\n6. Small Values\n";
        cout << "Enter (1-6): ";
        int inp; cin >> inp;

        cout << "Array size (5-15 for step view): ";
        int sz; cin >> sz;

        string types[] = {"random","sorted","reverse","nearly","large","small"};
        vector<int> arr = generateInput(types[inp-1], sz);

        switch(algo) {
            case 1: bubbleSort(arr, true); break;
            case 2: selectionSort(arr, true); break;
            case 3: insertionSort(arr, true); break;
            case 4: quickSort(arr, true); break;
            case 5: heapSort(arr, true); break;
            case 6: countingSort(arr, true); break;
        }

    } else if (choice == 2) {
        cout << "\nSelect input type:\n";
        cout << "1. Random\n2. Sorted\n3. Reverse\n4. Nearly Sorted\n5. Large Values\n6. Small Values\n";
        cout << "Enter (1-6): ";
        int inp; cin >> inp;
        cout << "Array size: ";
        int sz; cin >> sz;
        string types[] = {"random","sorted","reverse","nearly","large","small"};
        compareAll(types[inp-1], sz);

    } else if (choice == 3) {
        int sz = 1000;
        cout << "\nRunning all input types with size " << sz << "...\n";
        string types[] = {"random","sorted","reverse","nearly","large","small"};
        for (auto& t : types) compareAll(t, sz);
    }

    return 0;
}