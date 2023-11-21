package jp.sourceforge.qrcode.exception;

/**
 * When an invalid data block is found during the decoding of a QR code, an exception is thrown.
 */
public class InvalidDataBlockException extends IllegalArgumentException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidDataBlockException(String message) {
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
