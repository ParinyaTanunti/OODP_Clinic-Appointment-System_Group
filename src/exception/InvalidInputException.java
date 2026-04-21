package exception;

/**
 * InvalidInputException
 *
 * WHY CUSTOM EXCEPTION:
 * Used whenever user input fails validation (empty name, bad date format, etc.).
 * Keeping input errors as a dedicated exception type lets us catch and handle
 * them separately from system errors like file I/O failures.
 * This is concept 2.3 (Custom Exception).
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super("Invalid input: " + message);
    }
}
