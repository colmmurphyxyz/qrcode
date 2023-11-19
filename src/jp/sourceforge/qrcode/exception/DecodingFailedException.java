package jp.sourceforge.qrcode.exception;

/**
 Exception thrown during QR code decoding failures, potentially due to symbol absence leading to
 issues like missing patterns or alignment. Symbol data errors may result in illegal data blocks,
 causing problems like invalid version information or unsupported versions.
 */
public class DecodingFailedException extends IllegalArgumentException {
    String message;

    /**
     * Constructs the exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public DecodingFailedException(String message) {
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
