package model;

/**
 * Appointment class
 *
 * Holds the link between a Patient and a Doctor for a specific date/time slot.
 * Uses composition — it contains references to Patient and Doctor objects.
 */
public class Appointment {

    private String appointmentId;
    private Patient patient;
    private Doctor  doctor;
    private String  date;
    private String  timeSlot;
    private String  status; // "ACTIVE" or "CANCELLED"

    public Appointment(String appointmentId, Patient patient, Doctor doctor,
                       String date, String timeSlot) {
        this.appointmentId = appointmentId;
        this.patient       = patient;
        this.doctor        = doctor;
        this.date          = date;
        this.timeSlot      = timeSlot;
        this.status        = "ACTIVE";
    }

    // Returns a formatted summary of the appointment
    public String getDetails() {
        return String.format(
            "[%s] ID: %s | Patient: %s | Doctor: %s | Date: %s | Slot: %s",
            status, appointmentId, patient.getName(),
            doctor.getName(), date, timeSlot
        );
    }

    /**
     * Cancel this appointment and free the doctor's time slot back.
     */
    public void cancel() {
        this.status = "CANCELLED";
        doctor.cancelSlot(timeSlot); // Give slot back to doctor
    }

    // Getters
    public String  getAppointmentId() { return appointmentId; }
    public Patient getPatient()       { return patient; }
    public Doctor  getDoctor()        { return doctor; }
    public String  getDate()          { return date; }
    public String  getTimeSlot()      { return timeSlot; }
    public String  getStatus()        { return status; }
    public void    setStatus(String s){ this.status = s; }

    /**
     * Convert to CSV line for file storage.
     * Format: appointmentId,patientId,doctorId,date,timeSlot,status
     */
    public String toFileString() {
        return appointmentId + "," + patient.getUserId() + "," +
               doctor.getUserId() + "," + date + "," + timeSlot + "," + status;
    }
}