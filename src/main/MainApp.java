package main;

import services.ClinicManager;
import services.FileManager;
import exception.DoctorNotFoundException;
import exception.SlotUnavailableException;
import exception.InvalidInputException;

import java.io.IOException;
import java.util.Scanner;

/**
 * MainApp — Entry point of the Clinic Appointment System
 *
 * WHY Scanner HERE ONLY:
 * All keyboard input is handled in MainApp so that model and service
 * classes stay testable and independent of System.in.
 * This is concept 2.4 (Input and Output from Keyboard).
 */
public class MainApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClinicManager clinic = new ClinicManager();

    public static void main(String[] args) {
        FileManager.initialize();

        try {
            clinic.loadAll();
        } catch (IOException e) {
            System.out.println("Could not load data: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            clearScreen();
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1: menuManagePatients(); break;
                case 2: menuManageDoctors();  break;
                case 3: menuBookAppointment(); break;
                case 4: menuViewAppointments(); break;
                case 5: menuCancelAppointment(); break;
                case 6:
                    try {
                        clinic.saveAll();
                    } catch (IOException e) {
                        System.out.println("Error saving: " + e.getMessage());
                    }
                    clearScreen();
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    pause();
            }
        }
        scanner.close();
    }

    // ─── MENUS ───────────────────────────────────────────────────────────────

    private static void printMainMenu() {
        System.out.println("========================================");
        System.out.println("      Clinic Appointment System");
        System.out.println("========================================");
        System.out.println("1. Manage Patients");
        System.out.println("2. Manage Doctors");
        System.out.println("3. Book Appointment");
        System.out.println("4. View Appointments");
        System.out.println("5. Cancel Appointment");
        System.out.println("6. Save & Exit");
        System.out.print("Choose an option: ");
    }

    private static void menuManagePatients() {
        clearScreen();
        System.out.println("=== Manage Patients ===");
        System.out.println("1. Add Patient");
        System.out.println("2. View All Patients");
        System.out.print("Choose: ");
        int choice = readInt();

        if (choice == 1) {
            clearScreen();
            System.out.println("=== Add Patient ===");
            try {
                System.out.print("Name         : "); String name = scanner.nextLine().trim();
                System.out.print("Phone        : "); String phone = scanner.nextLine().trim();
                System.out.print("Date of Birth (YYYY-MM-DD): "); String dob = scanner.nextLine().trim();
                clinic.addPatient(name, phone, dob);
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }

        } else if (choice == 2) {
            clearScreen();
            clinic.listPatients();
        }
        pause();
    }

    private static void menuManageDoctors() {
        clearScreen();
        System.out.println("=== Manage Doctors ===");
        System.out.println("1. Add Doctor");
        System.out.println("2. View All Doctors");
        System.out.print("Choose: ");
        int choice = readInt();

        if (choice == 1) {
            clearScreen();
            System.out.println("=== Add Doctor ===");
            try {
                System.out.print("Name         : "); String name = scanner.nextLine().trim();
                System.out.print("Phone        : "); String phone = scanner.nextLine().trim();
                System.out.print("Specialty    : "); String spec = scanner.nextLine().trim();
                System.out.print("Time slots (comma-separated, e.g. 09:00,10:00,14:00): ");
                String slots = scanner.nextLine().trim();
                clinic.addDoctor(name, phone, spec, slots);
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }

        } else if (choice == 2) {
            clearScreen();
            clinic.listDoctors();
        }
        pause();
    }

    private static void menuBookAppointment() {
        clearScreen();
        System.out.println("=== Book Appointment ===");

        // Show patients
        clinic.listPatients();
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine().trim();

        // Show doctors with their slots clearly
        System.out.println();
        clinic.listDoctors();
        System.out.print("Enter Doctor ID : ");
        String doctorId = scanner.nextLine().trim();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        System.out.print("Time Slot (e.g. 09:00): ");
        String slot = scanner.nextLine().trim();

        try {
            clinic.bookAppointment(patientId, doctorId, date, slot);
        } catch (DoctorNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SlotUnavailableException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
        pause();
    }

    private static void menuViewAppointments() {
        clearScreen();
        System.out.println("=== View Appointments ===");
        System.out.println("1. View All");
        System.out.println("2. View by Patient");
        System.out.print("Choose: ");
        int choice = readInt();

        clearScreen();
        if (choice == 1) {
            clinic.viewAllAppointments();
        } else if (choice == 2) {
            clinic.listPatients();
            System.out.print("Enter Patient ID: ");
            String pid = scanner.nextLine().trim();
            clinic.viewAppointmentsByPatient(pid);
        }
        pause();
    }

    private static void menuCancelAppointment() {
        clearScreen();
        System.out.println("=== Cancel Appointment ===");
        clinic.viewAllAppointments();
        System.out.print("Enter Appointment ID to cancel: ");
        String apptId = scanner.nextLine().trim();
        try {
            clinic.cancelAppointment(apptId);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
        pause();
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    /**
     * WHY clearScreen():
     * Clears the terminal between menus so the display stays clean.
     * Works on both Windows (\033[H\033[2J ANSI) and falls back to
     * cls/clear if ANSI is not supported.
     */
    private static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear")
                    .inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback: print blank lines
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    /**
     * WHY pause():
     * Waits for user to press Enter before clearing the screen,
     * so they have time to read the output.
     */
    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * WHY readInt():
     * Wraps parseInt in try-catch so non-numeric input never crashes the program.
     * This is part of concept 2.4 (Input handling).
     */
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}