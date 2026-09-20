@rem Minimal gradlew.bat launcher
@if "%DEBUG%"=="" @echo off
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
java -jar "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" %*
