package jp.sourceforge.qrcode.exception;

/**
 * When a symbol cannot be located during QR code decoding, an exception is thorwn.
 */
public class SymbolNotFoundException extends IllegalArgumentException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public SymbolNotFoundException(String message) {
        this.message = message;
    }

    /**
     * Retrieves the detail message associated with this exception.
     *
     * @return the detail message.
     */
    public String getMessage() {
        return message;
    }
}

