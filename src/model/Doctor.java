package model;

import interfaces.ISchedulable;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor class
 *
 * WHY EXTENDS Person AND IMPLEMENTS ISchedulable:
 * Doctor IS-A Person (inherits userId, name, phone).
 * Doctor also CAN-BE-SCHEDULED, so it implements ISchedulable.
 * Using both inheritance and interface together shows concept 2.2 fully —
 * we get code reuse from Person AND a behavioral contract from ISchedulable.
 */
public class Doctor extends Person implements ISchedulable {

    private String specialty;
    private List<String> availableSlots; // concept 2.5: Collection with Generics

    public Doctor(String userId, String name, String phone, String specialty) {
        super(userId, name, phone);
        this.specialty      = specialty;
        this.availableSlots = new ArrayList<>();
    }

    /**
     * WHY OVERRIDE displayInfo():
     * Doctor shows specialty and available slot count,
     * which is different from Patient's display.
     */
    @Override
    public void displayInfo() {
        System.out.println("-----------------------------");
        System.out.println("Doctor ID    : " + userId);
        System.out.println("Name         : " + name);
        System.out.println("Phone        : " + phone);
        System.out.println("Specialty    : " + specialty);
        System.out.println("Open Slots   : " + availableSlots.size());
        System.out.println("-----------------------------");
    }

    /**
     * WHY IMPLEMENT getAvailableSlots():
     * ISchedulable contract requires this — returns all free slots for this doctor.
     */
    @Override
    public List<String> getAvailableSlots() {
        return availableSlots;
    }

    /**
     * WHY IMPLEMENT schedule():
     * Removes the slot from the available list so no one else can book it.
     */
    @Override
    public void schedule(String slot) {
        availableSlots.remove(slot);
    }

    /**
     * WHY IMPLEMENT cancelSlot():
     * Adds the slot back so it becomes available again after a cancellation.
     */
    @Override
    public void cancelSlot(String slot) {
        if (!availableSlots.contains(slot)) {
            availableSlots.add(slot);
        }
    }

    // Add a new available time slot
    public void addSlot(String slot) {
        if (!availableSlots.contains(slot)) {
            availableSlots.add(slot);
        }
    }

    // Getters
    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String s) {
        this.specialty = s;
    }

    public void setAvailableSlots(List<String> slots) {
        this.availableSlots = slots;
    }

    /**
     * Convert doctor data to CSV line for file storage.
     * Format: userId,name,phone,specialty,slot1|slot2|...
     */
    public String toFileString() {
        String slots = String.join("|", availableSlots);
        return userId + "," + name + "," + phone + "," + specialty + "," + slots;
    }
}
