@echo off
REM Windows batch build script for Clinic Appointment System
REM Uses explicit file paths (no globs) to avoid javac "Invalid filename" errors

setlocal
cd /d "%~dp0"

if not exist "out" mkdir out

echo Compiling...
javac -d out -cp src ^
    src/interfaces/ISchedulable.java ^
    src/model/Person.java ^
    src/model/Patient.java ^
    src/model/Doctor.java ^
    src/model/Appointment.java ^
    src/exception/DoctorNotFoundException.java ^
    src/exception/SlotUnavailableException.java ^
    src/exception/InvalidInputException.java ^
    src/services/FileManager.java ^
    src/services/ClinicManager.java ^
    src/main/MainApp.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    exit /b %ERRORLEVEL%
)

echo Compilation successful.

if "%1"=="run" (
    echo Running application...
    java -cp out main.MainApp
)
