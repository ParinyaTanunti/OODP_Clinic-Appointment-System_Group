package interfaces;

import java.util.List;

/**
 * ISchedulable interface
 *
 * WHY INTERFACE:
 * We use an interface here instead of an abstract class because ISchedulable
 * defines a CONTRACT — any class that can be scheduled MUST implement these
 * methods. This allows future classes (e.g., Nurse, Specialist) to also be
 * schedulable without being forced into the Person hierarchy.
 * This is concept 2.2 (Interface).
 */
public interface ISchedulable {

    // Returns a list of available time slots for this schedulable entity
    List<String> getAvailableSlots();

    // Books a specific time slot
    void schedule(String slot);

    // Frees up a previously booked time slot
    void cancelSlot(String slot);
}