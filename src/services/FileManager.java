package services;

import model.Patient;
import model.Doctor;
import model.Appointment;

import java.io.*;
import java.util.*;

/**
 * FileManager
 *
 * WHY SEPARATE CLASS FOR FILE I/O:
 * Keeping all read/write logic in one class follows the Single Responsibility
 * Principle — ClinicManager handles business logic, FileManager handles
 * persistence. If we change the file format later, only this class needs to change.
 * This is concept 2.1 (Read/Write from File).
 */
public class FileManager {

    private static final String DATA_DIR          = "data/";
    private static final String PATIENTS_FILE     = DATA_DIR + "patients.txt";
    private static final String DOCTORS_FILE      = DATA_DIR + "doctors.txt";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.txt";

    // Ensure data directory exists on startup
    public static void initialize() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();
        try {
            new File(PATIENTS_FILE).createNewFile();
            new File(DOCTORS_FILE).createNewFile();
            new File(APPOINTMENTS_FILE).createNewFile();
        } catch (IOException e) {
            System.out.println("Warning: could not create data files: " + e.getMessage());
        }
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────

    /**
     * WHY WE WRITE ALL AT ONCE:
     * Overwriting the file on each save ensures no stale data remains.
     * We write every object using its toFileString() method so the format
     * stays consistent and easy to read back.
     */
    public static void savePatients(List<Patient> patients) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient p : patients) {
                pw.println(p.toFileString());
            }
        }
    }

    public static void saveDoctors(List<Doctor> doctors) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor d : doctors) {
                pw.println(d.toFileString());
            }
        }
    }

    public static void saveAppointments(List<Appointment> appointments) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment a : appointments) {
                pw.println(a.toFileString());
            }
        }
    }

    // ─── LOAD ────────────────────────────────────────────────────────────────

    /**
     * WHY BUFFEREDREADER:
     * BufferedReader reads line by line efficiently without loading the whole
     * file into memory. We split each line by comma to reconstruct objects.
     * This is concept 2.1 (Read from File).
     */
    public static List<Patient> loadPatients() throws IOException {
        List<Patient> list = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists() || file.length() == 0) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length < 4) continue;

                Patient p = new Patient(parts[0], parts[1], parts[2], parts[3]);
                // Restore medical history if present
                if (parts.length == 5 && !parts[4].isEmpty()) {
                    for (String note : parts[4].split("\\|")) {
                        p.addHistory(note);
                    }
                }
                list.add(p);
            }
        }
        return list;
    }

    public static List<Doctor> loadDoctors() throws IOException {
        List<Doctor> list = new ArrayList<>();
        File file = new File(DOCTORS_FILE);
        if (!file.exists() || file.length() == 0) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length < 4) continue;

                Doctor d = new Doctor(parts[0], parts[1], parts[2], parts[3]);
                // Restore available slots if present
                if (parts.length == 5 && !parts[4].isEmpty()) {
                    for (String slot : parts[4].split("\\|")) {
                        d.addSlot(slot);
                    }
                }
                list.add(d);
            }
        }
        return list;
    }

    public static List<Appointment> loadAppointments(List<Patient> patients,
                                                      List<Doctor> doctors) throws IOException {
        List<Appointment> list = new ArrayList<>();
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists() || file.length() == 0) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;

                // Find matching Patient and Doctor by ID
                Patient patient = findById(patients, parts[1]);
                Doctor  doctor  = findById(doctors,  parts[2]);
                if (patient == null || doctor == null) continue;

                Appointment a = new Appointment(
                    parts[0], patient, doctor, parts[3], parts[4]
                );
                a.setStatus(parts[5]);
                list.add(a);
            }
        }
        return list;
    }

    /**
     * Generic helper to find any Person subtype by ID.
     *
     * WHY GENERIC METHOD:
     * Instead of writing findPatientById() and findDoctorById() separately,
     * one generic method handles both. The type bound <T extends Person>
     * ensures we can only pass lists of Person subclasses.
     * This is concept 2.6 (Parametric Polymorphism).
     */
    public static <T extends model.Person> T findById(List<T> list, String id) {
        for (T item : list) {
            if (item.getUserId().equals(id)) return item;
        }
        return null;
    }
}