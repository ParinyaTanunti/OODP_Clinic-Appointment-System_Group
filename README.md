# 🏥 Clinic Appointment System

### 📖 Overview
The **Clinic Appointment System** is a Java-based console application designed to streamline the process of managing clinic appointments. Patients can register and book appointments with available doctors, while the system handles scheduling conflicts, stores records persistently, and provides a clean menu-driven interface for all interactions.

---

### ✨ Features

- 👤 **Patient & Doctor Management**
  Register new patients and doctors, view profiles, and manage their information.

- 📅 **Appointment Booking**
  Book appointments by selecting an available doctor and time slot, with automatic conflict detection.

- 📋 **View Appointments**
  View all appointments, or filter by patient or doctor.

- ❌ **Cancel Appointment**
  Cancel an existing appointment and free up the time slot.

- 💾 **Data Persistence**
  All patient, doctor, and appointment data is saved to and loaded from `.txt` files, so nothing is lost between sessions.

- ✅ **Input Validation**
  All user input is validated with clear error messages to ensure data integrity.

---

### 🛠️ Technologies Used

- Java SE
- Object-Oriented Programming (OOP)
- File I/O (for data persistence using `.txt` files)
- Exception Handling (custom exceptions)
- Java Collections Framework (`List`, `ArrayList`)
- Inheritance & Interface
- Generics & Parametric Polymorphism
- `Scanner` for console input/output

---

## 🧠 OOP Concepts Applied (Section 2.2)

| Concept | How It's Used |
|---|---|
| **2.1 File I/O** | `FileManager` reads and writes patient, doctor, and appointment data to `.txt` files to ensure data persists across sessions. |
| **2.2 Inheritance** | `Patient` and `Doctor` both extend the abstract `Person` class, sharing common attributes like `id`, `name`, and `displayInfo()`. |
| **2.2 Interface** | `Doctor` implements the `ISchedulable` interface, which enforces the `getAvailableSlots()` and `schedule()` methods. |
| **2.3 Exception Handling** | Custom exceptions `DoctorNotFoundException`, `SlotUnavailableException`, and `InvalidInputException` handle error scenarios gracefully. |
| **2.4 Keyboard I/O** | `Scanner` is used throughout `MainApp` to receive all user input from the console. |
| **2.5 Collections** | `ClinicManager` uses `List<Patient>`, `List<Doctor>`, and `List<Appointment>` to manage all records dynamically. |
| **2.6 Parametric Polymorphism** | A generic method `<T extends Person> T findById(List<T> list, String id)` searches for any person type by ID without duplicating code. |

---

### 📁 Project Structure

```
ClinicAppointmentSystem/
├── src/
│   ├── main/
│   │   └── MainApp.java
│   ├── model/
│   │   ├── Person.java          (abstract class)
│   │   ├── Patient.java         (extends Person)
│   │   ├── Doctor.java          (extends Person, implements ISchedulable)
│   │   └── Appointment.java
│   ├── interfaces/
│   │   └── ISchedulable.java    (interface)
│   ├── services/
│   │   ├── ClinicManager.java
│   │   └── FileManager.java
│   └── exception/
│       ├── DoctorNotFoundException.java
│       ├── SlotUnavailableException.java
│       └── InvalidInputException.java
└── data/
    ├── patients.txt
    ├── doctors.txt
    └── appointments.txt
```

---

### 🚀 How to Installation & Running

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/ClinicAppointmentSystem.git
   ```

2. **Navigate to the project directory**
   ```bash
   cd ClinicAppointmentSystem
   ```

3. **Compile the Java files**
   ```bash
   javac -d out -cp src src/main/MainApp.java src/model/*.java src/interfaces/*.java src/services/*.java src/exception/*.java
   ```

4. **Run the application**
   ```bash
   java -cp out main.MainApp
   ```

### Build on Windows

On Windows, glob patterns like `src/*/*.java` are not expanded reliably by the shell and can cause `javac` to fail with "Invalid filename". Use one of these methods instead:

- **PowerShell (recommended):** Run `.\build.ps1` to compile. Add `-Run` to also run the app:
  ```powershell
  .\build.ps1 -Run
  ```
- **Command Prompt:** Run `build.bat` to compile, or `build.bat run` to compile and run.

---

### 🖥️ Main Menu

```
=== Clinic Appointment System ===
1. Manage Patients
2. Manage Doctors
3. Book Appointment
4. View Appointments
5. Cancel Appointment
6. Save & Exit
Choose an option:
```

---

## 🧑‍🤝‍🧑 Group Members

| Name | Student ID | Section |
|---|---|---|
| Parinya Tanunti | 6831503057 | 1 |
| Nonnaphat Thirawatthanakamchon | 6831503053 | 1 |
| Acharayu Majan | 6831503081 | 1 |
| Nattasira Luecha | 6831503041 | 1 |

---

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## 🙏 Acknowledgments

Special thanks to our instructor **Wacharawan Intayoad** and our peers for their guidance and support throughout the development of this project.

---
> **Course:** [2/2567] 1305104 Object Oriented Design and Programming
> **Instructor:** Wacharawan Intayoad
