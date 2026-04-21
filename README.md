# Clinic Appointment System

## Overview
Clinic Appointment System is a Java console application for managing patients, doctors, and appointments. The program supports persistent storage using text files, validates user input, and prevents double-booking for the same doctor, date, and time slot.

## Features
- Add and view patients
- Add and view doctors
- Book appointments with conflict checking
- View all appointments
- View appointments by patient
- View appointments by doctor
- Cancel appointments
- Save and load data from text files

## Project Structure
```text
OODP_Clinic-Appointment-System_Group/
|-- src/
|   |-- main/
|   |   `-- MainApp.java
|   |-- model/
|   |   |-- Person.java
|   |   |-- Patient.java
|   |   |-- Doctor.java
|   |   `-- Appointment.java
|   |-- interfaces/
|   |   `-- ISchedulable.java
|   |-- services/
|   |   |-- ClinicManager.java
|   |   `-- FileManager.java
|   `-- exception/
|       |-- DoctorNotFoundException.java
|       |-- SlotUnavailableException.java
|       `-- InvalidInputException.java
|-- data/
|   |-- patients.txt
|   |-- doctors.txt
|   `-- appointments.txt
|-- build.ps1
`-- build.bat
```

## Requirements
- Java JDK 17 or later
- PowerShell or Command Prompt on Windows

## Clone the Repository
```bash
git clone https://github.com/ParinyaTanunti/OODP_Clinic-Appointment-System_Group.git
cd OODP_Clinic-Appointment-System_Group
```

## Build and Run

### PowerShell
Compile:
```powershell
.\build.ps1
```

Compile and run:
```powershell
.\build.ps1 -Run
```

### Command Prompt
Compile:
```bat
build.bat
```

Compile and run:
```bat
build.bat run
```

## Manual Compilation
If you prefer to compile manually, use:
```bash
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
```

Run:
```bash
java -cp out main.MainApp
```

## Notes
- Date input must use `YYYY-MM-DD`.
- Time slot input must use `HH:MM`.
- Data is stored in the `data/` folder.
- Text-file storage now escapes reserved separators so commas and pipe characters inside saved text do not corrupt later loads.

## Group Members
| Name | Student ID | Section |
|---|---|---|
| Parinya Tanunti | 6831503057 | 1 |
| Nonnaphat Thirawatthanakamchon | 6831503053 | 1 |
| Acharayu Majan | 6831503081 | 1 |
| Nattasira Luecha | 6831503041 | 1 |

## Course
- Course: 1305104 Object Oriented Design and Programming
- Semester: 2/2567
- Instructor: Wacharawan Intayoad
