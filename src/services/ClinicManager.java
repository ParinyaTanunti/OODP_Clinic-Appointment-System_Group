package services;

import model.Patient;
import model.Doctor;
import model.Appointment;
import exception.DoctorNotFoundException;
import exception.SlotUnavailableException;
import exception.InvalidInputException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
    public void addPatient(String name, String phone, String dob, String symptoms)
            throws InvalidInputException, IOException {
        validatePersonName(name, "Patient");
        if (phone == null || phone.trim().isEmpty())
            throw new InvalidInputException("Phone number cannot be empty.");
        if (dob == null || dob.trim().isEmpty())
            throw new InvalidInputException("Date of birth cannot be empty.");
        if (symptoms == null || symptoms.trim().isEmpty())
            throw new InvalidInputException("Symptoms cannot be empty.");

        String cleanDob = dob.trim();
        try {
            LocalDate.parse(cleanDob);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date of birth must be in YYYY-MM-DD format.");
        }

        String id = "P" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Patient p = new Patient(id, name.trim(), phone.trim(), cleanDob);
        p.addHistory(symptoms.trim());
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
        validatePersonName(name, "Doctor");
        if (phone == null || phone.trim().isEmpty())
            throw new InvalidInputException("Phone number cannot be empty.");
        if (specialty == null || specialty.trim().isEmpty())
            throw new InvalidInputException("Specialty cannot be empty.");
        if (slotsInput == null || slotsInput.trim().isEmpty())
            throw new InvalidInputException("At least one time slot is required.");

        String id = "D" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Doctor d = new Doctor(id, name.trim(), phone.trim(), specialty.trim());

        // Parse comma-separated time slots e.g. "09:00,10:00,14:00"
        for (String slot : slotsInput.split(",")) {
            String cleanSlot = slot.trim();
            if (cleanSlot.isEmpty()) {
                continue;
            }

            try {
                LocalTime.parse(cleanSlot);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Time slots must use HH:MM format.");
            }
            d.addSlot(cleanSlot);
        }

        if (d.getAvailableSlots().isEmpty()) {
            throw new InvalidInputException("At least one valid time slot is required.");
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

        String cleanDate = date.trim();
        String cleanSlot = slot.trim();

        try {
            LocalDate.parse(cleanDate);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date must be in YYYY-MM-DD format.");
        }

        try {
            LocalTime.parse(cleanSlot);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Time slot must be in HH:MM format.");
        }

        Patient patient = FileManager.findById(patients, patientId);
        if (patient == null)
            throw new InvalidInputException("Patient ID not found: " + patientId);

        Doctor doctor = FileManager.findById(doctors, doctorId);
        if (doctor == null)
            throw new DoctorNotFoundException(doctorId); // concept 2.3

        if (!doctor.getAvailableSlots().contains(cleanSlot))
            throw new SlotUnavailableException(cleanSlot); // concept 2.3

        // WHY CHECK APPOINTMENTS LIST:
        // Doctor availableSlots stores standard working hours, not date-specific bookings.
        // We therefore prevent double-booking by checking doctor + date + time among ACTIVE appointments.
        if (isDoctorBooked(doctorId, cleanDate, cleanSlot))
            throw new SlotUnavailableException(cleanSlot + " on " + cleanDate);

        String apptId = "A" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Appointment appt = new Appointment(apptId, patient, doctor, cleanDate, cleanSlot);

        appointments.add(appt);
        FileManager.saveAppointments(appointments);
        System.out.println("Appointment booked! ID: " + apptId);
    }

    private boolean isDoctorBooked(String doctorId, String date, String slot) {
        for (Appointment a : appointments) {
            if (a.getStatus().equals("ACTIVE")
                    && a.getDoctor().getUserId().equals(doctorId)
                    && a.getDate().equals(date)
                    && a.getTimeSlot().equals(slot)) {
                return true;
            }
        }
        return false;
    }

    public boolean cancelAppointment(String apptId) throws IOException {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(apptId)) {
                if (a.getStatus().equals("CANCELLED")) {
                    System.out.println("This appointment is already cancelled.");
                    return true;
                }
                a.cancel();
                FileManager.saveAppointments(appointments);
                System.out.println("Appointment " + apptId + " cancelled.");
                return true;
            }
        }
        System.out.println("Appointment ID not found: " + apptId);
        return false;
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
        Patient patient = FileManager.findById(patients, patientId);
        if (patient == null) {
            System.out.println("Patient ID not found: " + patientId);
            return;
        }

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

    public void viewAppointmentsByDoctor(String doctorId) {
        Doctor doctor = FileManager.findById(doctors, doctorId);
        if (doctor == null) {
            System.out.println("Doctor ID not found: " + doctorId);
            return;
        }

        boolean found = false;
        System.out.println("\n=== Appointments for Doctor " + doctorId + " ===");
        for (Appointment a : appointments) {
            if (a.getDoctor().getUserId().equals(doctorId)) {
                System.out.println(a.getDetails());
                found = true;
            }
        }
        if (!found) System.out.println("No appointments found for this doctor.");
    }

    private void validatePersonName(String name, String label) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException(label + " name cannot be empty.");
        }

        String cleanName = name.trim();
        if (cleanName.matches(".*\\d.*")) {
            throw new InvalidInputException(label + " name cannot contain numbers.");
        }

        if (!cleanName.matches("[\\p{L} .'-]+")) {
            throw new InvalidInputException(label + " name contains invalid characters.");
        }
    }

    // Getters
    public List<Patient>     getPatients()     { return patients; }
    public List<Doctor>      getDoctors()      { return doctors; }
    public List<Appointment> getAppointments() { return appointments; }
}
