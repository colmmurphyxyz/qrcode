package jp.sourceforge.qrcode;

import java.nio.charset.Charset;

public class DecodeResult {
    private final int numCorrectionFailures;
    private final byte[] decodedBytes;

    public DecodeResult(byte[] decodedBytes, int numCorrectionFailures) {
        this.decodedBytes = decodedBytes;
        this.numCorrectionFailures = numCorrectionFailures;
    }

    public byte[] getDecodedBytes() {
        return this.decodedBytes;
    }

    public int getNumCorrectionFailures() {
        return this.numCorrectionFailures;
    }

    public String getDecodedText() {
        return new String(this.decodedBytes, Charset.defaultCharset());
    }

    public boolean isCorrectionSucceeded() {
        return this.numCorrectionFailures == 0;
    }
}
