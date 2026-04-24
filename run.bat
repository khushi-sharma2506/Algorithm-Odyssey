@echo off
setlocal enabledelayedexpansion
title AlgoVerse Build

echo ============================================================
echo   AlgoVerse - Build and Run Script
echo ============================================================

:: ── Locate Java ──────────────────────────────────────────────
where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. Please install JDK 17+ and add to PATH.
    pause & exit /b 1
)
for /f "tokens=*" %%v in ('java -version 2^>^&1') do (
    echo Java: %%v & goto :javaok
)
:javaok

:: ── Create output directory ───────────────────────────────────
if not exist out mkdir out
if not exist data mkdir data

:: ── Collect all .java files ───────────────────────────────────
echo.
echo [1/3] Collecting source files...
set SRC_FILES=
for /r src %%f in (*.java) do set SRC_FILES=!SRC_FILES! "%%f"

if "!SRC_FILES!"=="" (
    echo [ERROR] No .java files found under src\
    pause & exit /b 1
)

:: ── Compile ───────────────────────────────────────────────────
echo [2/3] Compiling...
javac -encoding UTF-8 -d out -sourcepath src !SRC_FILES!

if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed. Fix the errors above and retry.
    pause & exit /b 1
)
echo       Compilation successful!

:: ── Run ───────────────────────────────────────────────────────
echo [3/3] Launching AlgoVerse...
echo.
java -cp out Main

endlocal
