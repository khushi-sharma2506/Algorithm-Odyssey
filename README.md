# Algoverse

A desktop app that visualizes algorithms step by step — built with Java Swing as part of our DAA course at Graphic Era University.

## What it does

Instead of just reading about how Merge Sort or BFS works, you can watch it happen. Pick an algorithm, hit play, and see every comparison, swap, and recursive call animate in real time. You can slow it down, speed it up, or step through it manually.

There's also a gamification layer — you earn XP as you complete challenges, and there's a leaderboard to see how you stack up.

## Algorithms covered

- **Sorting** — Bubble, Selection, Insertion, Merge, Quick Sort
- **Recursion** — Tower of Hanoi, Fibonacci
- **Divide & Conquer** — Merge Sort, Quick Sort
- **Graph Algorithms** — BFS, DFS
- **Greedy Algorithms**
- **Dynamic Programming**

## Getting started

You need JDK 8 or higher. That's it.

```bash
git clone https://github.com/Akhil-00001/Algoverse.git
cd Algoverse
```

**On Windows**, use the included batch scripts:
```bash
compile.bat   # compiles everything
run.bat       # runs the app
```

**Manually:**
```bash
javac -d out src/**/*.java
java -cp out Main
```

## How it's built

Pure Java Swing — no external libraries or frameworks. User data and the leaderboard are stored locally in JSON files under `/data`, so the whole thing runs offline with no server needed.

## Team

Khushi Sharma · Roma Yadav · Akhil Kotnala · Alok Goyal
