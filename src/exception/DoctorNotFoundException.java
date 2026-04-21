package exception;

/**
 * DoctorNotFoundException
 *
 * WHY CUSTOM EXCEPTION:
 * A generic Exception gives no context about what went wrong.
 * By creating DoctorNotFoundException we make the error self-documenting —
 * any catch block that sees this type knows immediately that a doctor lookup
 * failed, not some unrelated I/O or null issue.
 * This is concept 2.3 (Custom Exception).
 */
public class DoctorNotFoundException extends Exception {
    public DoctorNotFoundException(String doctorId) {
        super("Doctor not found with ID: " + doctorId);
    }
}
