@echo off
set JAVA_HOME=c:\Users\Admin\.gemini\antigravity\playground\spinning-cosmos\java-1.8.0-openjdk-1.8.0.492.b09-1.win.jdk.x86_64
set PATH=%JAVA_HOME%\bin;%PATH%
echo ---
echo Java 8 Environment configured!
java -version
echo ---
echo Run 'mvn spring-boot:run' to start the backend (requires Maven installed on your system).
