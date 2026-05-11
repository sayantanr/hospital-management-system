@echo off
title Backend API

set "JAVA_HOME=c:\Users\Admin\.gemini\antigravity\playground\spinning-cosmos\java-1.8.0-openjdk-1.8.0.492.b09-1.win.jdk.x86_64"
set "MAVEN_HOME=c:\Users\Admin\.gemini\antigravity\playground\spinning-cosmos\hospital-management-system\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

mvn spring-boot:run
