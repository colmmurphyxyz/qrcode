package jp.sourceforge.qrcode.exception;

/**
 * When attempting to decode a QR code and encountering invalid version information, an exception is thrown.
 */
public class InvalidVersionInfoException extends VersionInformationException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidVersionInfoException(String message) {
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
