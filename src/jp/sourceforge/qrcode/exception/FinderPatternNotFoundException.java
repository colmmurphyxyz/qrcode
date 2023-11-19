package jp.sourceforge.qrcode.exception;

/**
 * When decoding a QR code and the finder pattern cannot be located, an exception is thrown.
 */
public class FinderPatternNotFoundException extends Exception {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public FinderPatternNotFoundException(String message) {
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
