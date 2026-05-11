@echo off
cd c:\Users\Admin\.gemini\antigravity\playground\spinning-cosmos\hospital-management-system

echo ===================================================
echo Setting up and starting Hospital Management System
echo ===================================================

echo [1/4] Checking Maven...
if not exist "apache-maven-3.9.6" (
    echo Downloading Maven...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'"
    tar -xf maven.zip
    del maven.zip
)

echo [2/4] Setting up Environment Variables...
set JAVA_HOME=c:\Users\Admin\.gemini\antigravity\playground\spinning-cosmos\java-1.8.0-openjdk-1.8.0.492.b09-1.win.jdk.x86_64
set MAVEN_HOME=%CD%\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo [3/4] Starting Java Backend in new window...
cd backend
start cmd /k ".\start_backend.bat"

echo [4/4] Starting Python Frontend in new window...
cd ../frontend
if not exist "venv" (
    echo Creating virtual environment...
    python -m venv venv
)
start cmd /k "title Flask UI && venv\Scripts\python.exe -m pip install -r requirements.txt && venv\Scripts\python.exe app.py"

echo Waiting 15 seconds for servers to initialize...
timeout /t 15

echo Opening Chrome...
start chrome http://localhost:8501

echo Done! The servers are running in separate terminal windows.
