package exception;

/**
 * SlotUnavailableException
 *
 * WHY CUSTOM EXCEPTION:
 * Thrown when a patient tries to book a time slot that is no longer available.
 * Separating this from DoctorNotFoundException lets the caller handle each
 * failure case differently (e.g., suggest another slot vs. suggest another doctor).
 * This is concept 2.3 (Custom Exception).
 */
public class SlotUnavailableException extends Exception {
    public SlotUnavailableException(String slot) {
        super("Time slot '" + slot + "' is not available.");
    }
}