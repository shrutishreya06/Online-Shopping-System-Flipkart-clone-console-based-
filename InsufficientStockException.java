/**
 * InsufficientStockException.java
 *
 * Custom exception thrown during checkout when a product in the
 * cart does not have enough stock left. This is a checked exception,
 * so any code calling checkout() must either catch it or declare it.
 */
public class InsufficientStockException extends Exception {

    public InsufficientStockException(String message) {
        super(message);
    }
}
