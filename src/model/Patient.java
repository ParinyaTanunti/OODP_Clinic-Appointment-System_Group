package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient class
 *
 * WHY EXTENDS Person:
 * Patient IS-A Person — it shares userId, name, phone from the parent class.
 * By extending Person we reuse all common attributes and only add fields
 * specific to patients (medicalHistory, dateOfBirth).
 * This is concept 2.2 (Inheritance) and 2.6 (Parametric Polymorphism via
 * List<String> for medicalHistory).
 */
public class Patient extends Person {

    private List<String> medicalHistory; // concept 2.5: Collection with Generics
    private String dateOfBirth;

    public Patient(String userId, String name, String phone, String dateOfBirth) {
        super(userId, name, phone); // Call Person constructor
        this.dateOfBirth     = dateOfBirth;
        this.medicalHistory  = new ArrayList<>();
    }

    /**
     * WHY OVERRIDE displayInfo():
     * Patient shows its own specific fields (DOB, history count),
     * which are different from what a Doctor would display.
     */
    @Override
    public void displayInfo() {
        System.out.println("-----------------------------");
        System.out.println("Patient ID   : " + userId);
        System.out.println("Name         : " + name);
        System.out.println("Phone        : " + phone);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("History Count: " + medicalHistory.size());
        System.out.println("-----------------------------");
    }

    // Add a note to this patient's medical history
    public void addHistory(String note) {
        medicalHistory.add(note);
    }

    // Getters
    public List<String> getMedicalHistory() { return medicalHistory; }
    public String getDateOfBirth()          { return dateOfBirth; }

    // Setter
    public void setMedicalHistory(List<String> history) {
        this.medicalHistory = history;
    }

    /**
     * Convert patient data to a single CSV line for file storage.
     * Format: userId,name,phone,dateOfBirth,history1|history2|...
     */
    public String toFileString() {
        String history = String.join("|", medicalHistory);
        return userId + "," + name + "," + phone + "," + dateOfBirth + "," + history;
    }
}