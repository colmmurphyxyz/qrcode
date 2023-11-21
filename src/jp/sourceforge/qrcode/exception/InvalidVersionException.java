package jp.sourceforge.qrcode.exception;

/**
 * When decoding a QR code and an invalid version is found, an exception is thrown.
 */
public class InvalidVersionException extends VersionInformationException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidVersionException(String message) {
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
