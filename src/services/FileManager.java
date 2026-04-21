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
        if (!dir.exists()) {
            // Create the data folder once before any file read/write happens
            dir.mkdir();
        }
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
                pw.println(serializePatient(p));
            }
        }
    }

    public static void saveDoctors(List<Doctor> doctors) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor d : doctors) {
                pw.println(serializeDoctor(d));
            }
        }
    }

    public static void saveAppointments(List<Appointment> appointments) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment a : appointments) {
                pw.println(serializeAppointment(a));
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
        if (!file.exists() || file.length() == 0) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = splitEscaped(line, ',', 5);
                if (parts.length < 4) {
                    continue;
                }

                Patient p = new Patient(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    unescape(parts[3])
                );

                // Restore saved symptom history from the data file
                if (parts.length == 5 && !parts[4].isEmpty()) {
                    for (String note : splitEscaped(unescape(parts[4]), '|', 0)) {
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
        if (!file.exists() || file.length() == 0) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = splitEscaped(line, ',', 5);
                if (parts.length < 4) {
                    continue;
                }

                Doctor d = new Doctor(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    unescape(parts[3])
                );

                // Restore configured doctor time slots from the data file
                if (parts.length == 5 && !parts[4].isEmpty()) {
                    for (String slot : splitEscaped(unescape(parts[4]), '|', 0)) {
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
        if (!file.exists() || file.length() == 0) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = splitEscaped(line, ',', 6);
                if (parts.length < 6) {
                    continue;
                }

                // Reconnect appointment records to the matching patient and doctor objects
                Patient patient = findById(patients, unescape(parts[1]));
                Doctor  doctor  = findById(doctors,  unescape(parts[2]));
                if (patient == null || doctor == null) {
                    continue;
                }

                Appointment a = new Appointment(
                    unescape(parts[0]),
                    patient,
                    doctor,
                    unescape(parts[3]),
                    unescape(parts[4])
                );
                a.setStatus(unescape(parts[5]));
                list.add(a);
            }
        }
        return list;
    }

    private static String serializePatient(Patient patient) {
        return String.join(",",
            escape(patient.getUserId()),
            escape(patient.getName()),
            escape(patient.getPhone()),
            escape(patient.getDateOfBirth()),
            escape(String.join("|", escapeList(patient.getMedicalHistory())))
        );
    }

    private static String serializeDoctor(Doctor doctor) {
        return String.join(",",
            escape(doctor.getUserId()),
            escape(doctor.getName()),
            escape(doctor.getPhone()),
            escape(doctor.getSpecialty()),
            escape(String.join("|", escapeList(doctor.getAvailableSlots())))
        );
    }

    private static String serializeAppointment(Appointment appointment) {
        return String.join(",",
            escape(appointment.getAppointmentId()),
            escape(appointment.getPatient().getUserId()),
            escape(appointment.getDoctor().getUserId()),
            escape(appointment.getDate()),
            escape(appointment.getTimeSlot()),
            escape(appointment.getStatus())
        );
    }

    private static List<String> escapeList(List<String> values) {
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            escaped.add(escape(value));
        }
        return escaped;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        // Escape reserved separators so text can be stored safely in one line
        StringBuilder escaped = new StringBuilder();
        for (char ch : value.toCharArray()) {
            if (ch == '\\' || ch == ',' || ch == '|') {
                escaped.append('\\');
            }
            escaped.append(ch);
        }
        return escaped.toString();
    }

    private static String unescape(String value) {
        StringBuilder plain = new StringBuilder();
        boolean escaping = false;

        for (char ch : value.toCharArray()) {
            if (escaping) {
                plain.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                plain.append(ch);
            }
        }

        if (escaping) {
            plain.append('\\');
        }
        return plain.toString();
    }

    private static String[] splitEscaped(String value, char delimiter, int limit) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        // Split text while respecting escaped commas and pipes
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (escaping) {
                current.append(ch);
                escaping = false;
                continue;
            }

            if (ch == '\\') {
                current.append(ch);
                escaping = true;
                continue;
            }

            if (ch == delimiter && (limit <= 0 || parts.size() < limit - 1)) {
                parts.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(ch);
        }

        parts.add(current.toString());
        return parts.toArray(new String[0]);
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
        // Reuse one generic search method for both patients and doctors
        for (T item : list) {
            if (item.getUserId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
