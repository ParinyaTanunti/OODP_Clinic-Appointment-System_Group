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

    // concept 2.4: Scanner for all keyboard input
    private static final Scanner scanner = new Scanner(System.in);
    private static final ClinicManager clinic = new ClinicManager();

    public static void main(String[] args) {
        FileManager.initialize();

        // concept 2.1 + 2.3: Load data from file, handle IOException
        try {
            clinic.loadAll();
        } catch (IOException e) {
            System.out.println("Could not load data: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
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
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // ─── MENUS ───────────────────────────────────────────────────────────────

    private static void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("    Clinic Appointment System");
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
        System.out.println("\n--- Manage Patients ---");
        System.out.println("1. Add Patient");
        System.out.println("2. View All Patients");
        System.out.print("Choose: ");
        int choice = readInt();
        if (choice == 1) {
            try {
                System.out.print("Name         : "); String name = scanner.nextLine().trim();
                System.out.print("Phone        : "); String phone = scanner.nextLine().trim();
                System.out.print("Date of Birth: "); String dob = scanner.nextLine().trim();
                clinic.addPatient(name, phone, dob);

            // concept 2.3: Catch custom exception and print specific message
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        } else if (choice == 2) {
            clinic.listPatients();
        }
    }

    private static void menuManageDoctors() {
        System.out.println("\n--- Manage Doctors ---");
        System.out.println("1. Add Doctor");
        System.out.println("2. View All Doctors");
        System.out.print("Choose: ");
        int choice = readInt();
        if (choice == 1) {
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
            clinic.listDoctors();
        }
    }

    private static void menuBookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        clinic.listPatients();
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine().trim();

        clinic.listDoctors();
        System.out.print("Enter Doctor ID : ");
        String doctorId = scanner.nextLine().trim();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        System.out.print("Time Slot (e.g. 09:00): ");
        String slot = scanner.nextLine().trim();

        try {
            clinic.bookAppointment(patientId, doctorId, date, slot);

        // concept 2.3: Each custom exception is caught separately for targeted feedback
        } catch (DoctorNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SlotUnavailableException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    private static void menuViewAppointments() {
        System.out.println("\n--- View Appointments ---");
        System.out.println("1. View All");
        System.out.println("2. View by Patient");
        System.out.print("Choose: ");
        int choice = readInt();
        if (choice == 1) {
            clinic.viewAllAppointments();
        } else if (choice == 2) {
            System.out.print("Enter Patient ID: ");
            String pid = scanner.nextLine().trim();
            clinic.viewAppointmentsByPatient(pid);
        }
    }

    private static void menuCancelAppointment() {
        clinic.viewAllAppointments();
        System.out.print("Enter Appointment ID to cancel: ");
        String apptId = scanner.nextLine().trim();
        try {
            clinic.cancelAppointment(apptId);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────

    /**
     * WHY SEPARATE readInt():
     * Wraps parseInt in a try-catch so the program never crashes from
     * a non-numeric menu input. This is part of concept 2.4 (Input handling).
     */
    private static int readInt() {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}