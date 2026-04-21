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
        while (true) {
            clearScreen();
            System.out.println("=== Manage Patients ===");
            System.out.println("1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            int choice = readInt();

            if (choice == 1) {
                while (true) {
                    clearScreen();
                    System.out.println("=== Add Patient ===");
                    System.out.println("Enter 0 at any time to cancel.");
                    try {
                        // Collect patient details from the user before saving
                        String name = readInputOrCancel("Name         : ");
                        if (name == null) {
                            break;
                        }
                        String phone = readInputOrCancel("Phone        : ");
                        if (phone == null) {
                            break;
                        }
                        String dob = readInputOrCancel("Date of Birth (YYYY-MM-DD): ");
                        if (dob == null) {
                            break;
                        }
                        String symptoms = readInputOrCancel("Symptoms     : ");
                        if (symptoms == null) {
                            break;
                        }
                        clinic.addPatient(name, phone, dob, symptoms);
                        pause();
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println("Error: " + e.getMessage());
                        System.out.print("\nPlease try again. Press Enter to re-enter patient details...");
                        scanner.nextLine();
                    } catch (IOException e) {
                        System.out.println("File error: " + e.getMessage());
                        pause();
                        return;
                    }
                }
            } else if (choice == 2) {
                clearScreen();
                clinic.listPatients();
                pause();
            } else if (choice == 0) {
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
                pause();
            }
        }
    }

    private static void menuManageDoctors() {
        while (true) {
            clearScreen();
            System.out.println("=== Manage Doctors ===");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            int choice = readInt();

            if (choice == 1) {
                while (true) {
                    clearScreen();
                    System.out.println("=== Add Doctor ===");
                    System.out.println("Enter 0 at any time to cancel.");
                    try {
                        // Collect doctor details and available time slots
                        String name = readInputOrCancel("Name         : ");
                        if (name == null) {
                            break;
                        }
                        String phone = readInputOrCancel("Phone        : ");
                        if (phone == null) {
                            break;
                        }
                        String spec = readInputOrCancel("Specialty    : ");
                        if (spec == null) {
                            break;
                        }
                        String slots = readInputOrCancel("Time slots (comma-separated, e.g. 09:00,10:00,14:00): ");
                        if (slots == null) {
                            break;
                        }
                        clinic.addDoctor(name, phone, spec, slots);
                        pause();
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println("Error: " + e.getMessage());
                        System.out.print("\nPlease try again. Press Enter to re-enter doctor details...");
                        scanner.nextLine();
                    } catch (IOException e) {
                        System.out.println("File error: " + e.getMessage());
                        pause();
                        return;
                    }
                }
            } else if (choice == 2) {
                clearScreen();
                clinic.listDoctors();
                pause();
            } else if (choice == 0) {
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
                pause();
            }
        }
    }

    private static void menuBookAppointment() {
        if (clinic.getPatients().isEmpty()) {
            clearScreen();
            System.out.println("=== Book Appointment ===");
            System.out.println("No patients registered. Please add a patient first.");
            pause();
            return;
        }

        if (clinic.getDoctors().isEmpty()) {
            clearScreen();
            System.out.println("=== Book Appointment ===");
            System.out.println("No doctors registered. Please add a doctor first.");
            pause();
            return;
        }

        while (true) {
            clearScreen();
            System.out.println("=== Book Appointment ===");
            System.out.println("Enter 0 at any time to cancel.");

            // Show current patients so the user can choose a valid ID
            clinic.listPatients();
            String patientId = readInputOrCancel("Enter Patient ID: ");
            if (patientId == null) {
                return;
            }

            // Show doctors and their slots before booking
            System.out.println();
            clinic.listDoctors();
            String doctorId = readInputOrCancel("Enter Doctor ID : ");
            if (doctorId == null) {
                return;
            }

            String date = readInputOrCancel("Date (YYYY-MM-DD): ");
            if (date == null) {
                return;
            }
            String slot = readInputOrCancel("Time Slot (e.g. 09:00): ");
            if (slot == null) {
                return;
            }

            try {
                // Only save the appointment if all booking rules pass
                clinic.bookAppointment(patientId, doctorId, date, slot);
                pause();
                return;
            } catch (DoctorNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (SlotUnavailableException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
                pause();
                return;
            }

            System.out.print("\nPlease try again. Press Enter to re-enter appointment details...");
            scanner.nextLine();
        }
    }

    private static void menuViewAppointments() {
        while (true) {
            clearScreen();
            System.out.println("=== View Appointments ===");
            System.out.println("1. View All");
            System.out.println("2. View by Patient");
            System.out.println("3. View by Doctor");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            int choice = readInt();

            clearScreen();
            if (choice == 1) {
                clinic.viewAllAppointments();
                pause();
            } else if (choice == 2) {
                if (clinic.getPatients().isEmpty()) {
                    System.out.println("No patients registered.");
                    pause();
                    continue;
                }

                clinic.listPatients();
                String pid = readInputOrCancel("Enter Patient ID (or 0 to go back): ");
                if (pid == null) {
                    continue;
                }
                clinic.viewAppointmentsByPatient(pid);
                pause();
            } else if (choice == 3) {
                if (clinic.getDoctors().isEmpty()) {
                    System.out.println("No doctors registered.");
                    pause();
                    continue;
                }

                clinic.listDoctors();
                String did = readInputOrCancel("Enter Doctor ID (or 0 to go back): ");
                if (did == null) {
                    continue;
                }
                clinic.viewAppointmentsByDoctor(did);
                pause();
            } else if (choice == 0) {
                return;
            } else {
                System.out.println("Invalid choice.");
                pause();
            }
        }
    }

    private static void menuCancelAppointment() {
        if (clinic.getAppointments().isEmpty()) {
            clearScreen();
            System.out.println("=== Cancel Appointment ===");
            System.out.println("No appointments on record.");
            pause();
            return;
        }

        while (true) {
            clearScreen();
            System.out.println("=== Cancel Appointment ===");
            clinic.viewAllAppointments();
            System.out.print("Enter Appointment ID to cancel (or 0 to go back): ");
            String apptId = scanner.nextLine().trim();

            if (apptId.equals("0")) {
                return;
            }

            try {
                boolean cancelled = clinic.cancelAppointment(apptId);
                if (cancelled) {
                    pause();
                    return;
                }

                System.out.print("\nPlease try again. Press Enter to re-enter appointment ID...");
                scanner.nextLine();
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
                pause();
                return;
            }
        }
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
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
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

    private static String readInputOrCancel(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        // Let the user exit the current form without finishing all fields
        if (input.equals("0")) {
            System.out.println("Action cancelled.");
            return null;
        }
        return input;
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
