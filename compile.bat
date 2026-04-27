@echo off
setlocal enabledelayedexpansion
set SRC_FILES=
for /r src %%f in (*.java) do set SRC_FILES=!SRC_FILES! "%%f"
javac -encoding UTF-8 -d out -sourcepath src !SRC_FILES!
if errorlevel 1 (
    echo Compilation failed
    exit /b 1
)
echo Compilation successful
exit /b 0
