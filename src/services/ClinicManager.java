package services;

import model.Patient;
import model.Doctor;
import model.Appointment;
import exception.DoctorNotFoundException;
import exception.SlotUnavailableException;
import exception.InvalidInputException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ClinicManager
 *
 * WHY THIS CLASS EXISTS:
 * All business logic lives here — adding patients/doctors, booking and
 * cancelling appointments. It delegates file I/O to FileManager, keeping
 * each class focused on one responsibility.
 *
 * Uses List<Patient>, List<Doctor>, List<Appointment> — concept 2.5
 * Throws custom exceptions — concept 2.3
 * Uses generic findById() from FileManager — concept 2.6
 */
public class ClinicManager {

    private List<Patient>     patients;     // concept 2.5: Collection with Generics
    private List<Doctor>      doctors;
    private List<Appointment> appointments;

    public ClinicManager() {
        this.patients     = new ArrayList<>();
        this.doctors      = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    // ─── LOAD & SAVE ─────────────────────────────────────────────────────────

    public void loadAll() throws IOException {
        patients     = FileManager.loadPatients();
        doctors      = FileManager.loadDoctors();
        appointments = FileManager.loadAppointments(patients, doctors);
        System.out.println("Data loaded successfully.");
    }

    public void saveAll() throws IOException {
        FileManager.savePatients(patients);
        FileManager.saveDoctors(doctors);
        FileManager.saveAppointments(appointments);
        System.out.println("Data saved successfully.");
    }

    // ─── PATIENT ─────────────────────────────────────────────────────────────

    /**
     * WHY VALIDATE INPUT HERE:
     * We throw InvalidInputException instead of silently accepting bad data.
     * This forces the caller (MainApp) to handle the error explicitly.
     */
    public void addPatient(String name, String phone, String dob)
            throws InvalidInputException, IOException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidInputException("Patient name cannot be empty.");
        if (phone == null || phone.trim().isEmpty())
            throw new InvalidInputException("Phone number cannot be empty.");
        if (dob == null || dob.trim().isEmpty())
            throw new InvalidInputException("Date of birth cannot be empty.");

        String id = "P" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Patient p = new Patient(id, name.trim(), phone.trim(), dob.trim());
        patients.add(p);
        FileManager.savePatients(patients);
        System.out.println("Patient added! ID: " + id);
    }

    public void listPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }
        System.out.println("\n=== Patients ===");
        for (Patient p : patients) p.displayInfo();
    }

    // ─── DOCTOR ──────────────────────────────────────────────────────────────

    public void addDoctor(String name, String phone, String specialty, String slotsInput)
            throws InvalidInputException, IOException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidInputException("Doctor name cannot be empty.");
        if (specialty == null || specialty.trim().isEmpty())
            throw new InvalidInputException("Specialty cannot be empty.");

        String id = "D" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Doctor d = new Doctor(id, name.trim(), phone.trim(), specialty.trim());

        // Parse comma-separated time slots e.g. "09:00,10:00,14:00"
        if (slotsInput != null && !slotsInput.trim().isEmpty()) {
            for (String slot : slotsInput.split(",")) {
                d.addSlot(slot.trim());
            }
        }
        doctors.add(d);
        FileManager.saveDoctors(doctors);
        System.out.println("Doctor added! ID: " + id);
    }

    public void listDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered.");
            return;
        }
        System.out.println("\n=== Doctors ===");
        for (Doctor d : doctors) {
            d.displayInfo();
            System.out.println("  Available slots: " + d.getAvailableSlots());
        }
    }

    // ─── APPOINTMENT ─────────────────────────────────────────────────────────

    /**
     * WHY THROWS DoctorNotFoundException AND SlotUnavailableException:
     * Each failure has a different cause and recovery path.
     * DoctorNotFoundException -> user should pick a valid doctor ID.
     * SlotUnavailableException -> user should pick a different time slot.
     * Catching them separately in MainApp gives targeted error messages.
     */
    public void bookAppointment(String patientId, String doctorId, String date, String slot)
            throws DoctorNotFoundException, SlotUnavailableException,
                   InvalidInputException, IOException {

        if (date == null || date.trim().isEmpty())
            throw new InvalidInputException("Date cannot be empty.");
        if (slot == null || slot.trim().isEmpty())
            throw new InvalidInputException("Time slot cannot be empty.");

        // Use generic findById — concept 2.6 (Parametric Polymorphism)
        Patient patient = FileManager.findById(patients, patientId);
        if (patient == null)
            throw new InvalidInputException("Patient ID not found: " + patientId);

        Doctor doctor = FileManager.findById(doctors, doctorId);
        if (doctor == null)
            throw new DoctorNotFoundException(doctorId); // concept 2.3

        if (!doctor.getAvailableSlots().contains(slot))
            throw new SlotUnavailableException(slot); // concept 2.3

        String apptId = "A" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Appointment appt = new Appointment(apptId, patient, doctor, date.trim(), slot.trim());

        doctor.schedule(slot); // Remove slot from doctor's available list
        appointments.add(appt);
        FileManager.saveDoctors(doctors);
        FileManager.saveAppointments(appointments);
        System.out.println("Appointment booked! ID: " + apptId);
    }

    public void cancelAppointment(String apptId) throws IOException {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(apptId)) {
                if (a.getStatus().equals("CANCELLED")) {
                    System.out.println("This appointment is already cancelled.");
                    return;
                }
                a.cancel();
                FileManager.saveDoctors(doctors);
                FileManager.saveAppointments(appointments);
                System.out.println("Appointment " + apptId + " cancelled.");
                return;
            }
        }
        System.out.println("Appointment ID not found: " + apptId);
    }

    public void viewAllAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments on record.");
            return;
        }
        System.out.println("\n=== All Appointments ===");
        for (Appointment a : appointments) {
            System.out.println(a.getDetails());
        }
    }

    public void viewAppointmentsByPatient(String patientId) {
        boolean found = false;
        System.out.println("\n=== Appointments for Patient " + patientId + " ===");
        for (Appointment a : appointments) {
            if (a.getPatient().getUserId().equals(patientId)) {
                System.out.println(a.getDetails());
                found = true;
            }
        }
        if (!found) System.out.println("No appointments found for this patient.");
    }

    // Getters
    public List<Patient>     getPatients()     { return patients; }
    public List<Doctor>      getDoctors()      { return doctors; }
    public List<Appointment> getAppointments() { return appointments; }
}