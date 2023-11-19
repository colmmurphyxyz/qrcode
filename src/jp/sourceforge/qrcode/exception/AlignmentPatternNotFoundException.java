package jp.sourceforge.qrcode.exception;

/**
 * When the alignment pattern cannot be located during QR code decoding, an exception is thrown.
 */
public class AlignmentPatternNotFoundException extends IllegalArgumentException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public AlignmentPatternNotFoundException(String message) {
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
