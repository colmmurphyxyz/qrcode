/*
 * created 2004/09/12
 */
package jp.sourceforge.qrcode;

import java.util.Vector;

import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.data.QRCodeSymbol;
import jp.sourceforge.qrcode.exception.DecodingFailedException;
import jp.sourceforge.qrcode.exception.InvalidDataBlockException;
import jp.sourceforge.qrcode.exception.SymbolNotFoundException;
import jp.sourceforge.qrcode.geom.Point;


import jp.sourceforge.qrcode.util.DebugCanvas;
import jp.sourceforge.qrcode.util.DebugCanvasAdapter;
import jp.sourceforge.qrcode.reader.QRCodeDataBlockReader;
import jp.sourceforge.qrcode.reader.QRCodeImageReader;
import jp.sourceforge.qrcode.ecc.RsDecode;

public class QRCodeDecoder {
    private QRCodeSymbol qrCodeSymbol;
    Vector<DecodeResult> results;
//    Vector<DecodeResult> lastResults = new Vector<>();
    private static DebugCanvas canvas;
    private QRCodeImageReader imageReader;
    private int numLastCorrectionFailures;
    private int numTryDecode;

    /**
     * Class to encapsulate the result of a QR code decoding operation
     * provides information about the number of error corrections and the decoded bytes in the qr code
     */
    public class DecodeResult {
        private final int numCorrectionFailures;
        private final byte[] decodedBytes;

        public DecodeResult(byte[] decodedBytes, int numCorrectionFailures) {
            this.decodedBytes = decodedBytes;
            this.numCorrectionFailures = numCorrectionFailures;
        }

        /**
         *
         * @return byte[] of the decoded bytes obtained by the QRCodeDecoder
         */
        public byte[] getDecodedBytes() {
            return decodedBytes;
        }

        public int getNumCorrectionFailures() {
            return numCorrectionFailures;
        }

        public boolean isCorrectionSucceeded() {
            return numLastCorrectionFailures == 0;
        }
    }

    public static void setCanvas(DebugCanvas canvas) {
        QRCodeDecoder.canvas = canvas;
    }

    public static DebugCanvas getCanvas() {
        return QRCodeDecoder.canvas;
    }

    public QRCodeDecoder() {
        numTryDecode = 0;
        results = new Vector<>();
        QRCodeDecoder.canvas = new DebugCanvasAdapter();
    }

    /**
     * Decodes a qr code from an image
     * @param qrCodeImage qr code image to decode
     * @return byte[] decoded byte array, can be interpreted as a character sequence, URL, or a binary blob
     *      depending on the use case
     * @throws DecodingFailedException if decoding fails
     */
    public byte[] decode(QRCodeImage qrCodeImage) throws DecodingFailedException {
        Point[] adjusts = getAdjustPoints();
        Vector<DecodeResult> results = new Vector<>();
        numTryDecode = 0;
        while (numTryDecode < adjusts.length) {
            try {
                DecodeResult result = decode(qrCodeImage, adjusts[numTryDecode]);
                if (result.isCorrectionSucceeded()) {
                    return result.getDecodedBytes();
                } else {
                    results.addElement(result);
                    canvas.println("Decoding succeeded but could not correct");
                    canvas.println("all errors. Retrying..");
                }
            } catch (DecodingFailedException dfe) {
                if (dfe.getMessage().contains("Finder Pattern"))
                    throw dfe;
            } finally {
                numTryDecode += 1;
            }
        }

        // image unrecognizable, cannot decode
        if (results.isEmpty()) {
            throw new DecodingFailedException("Give up decoding");
        }

        int minErrorIndex = -1;
        int minError = Integer.MAX_VALUE;
        for (int i = 0; i < results.size(); i++) {
            DecodeResult result = results.elementAt(i);
            if (result.getNumCorrectionFailures() < minError) {
                minError = result.getNumCorrectionFailures();
                minErrorIndex = i;
            }
        }
        canvas.println("All trials need for correct error");
        canvas.println("Reporting #" + (minErrorIndex) + " that,");
        canvas.println("corrected minimum errors (" + minError + ")");
        canvas.println("Decoding finished.");
        return (results.elementAt(minErrorIndex)).getDecodedBytes();
    }

    private Point[] getAdjustPoints() {
        // note that adjusts affect dependently
        // i.e. below means (0,0), (2,3), (3,4), (1,2), (2,1), (1,1), (-1,-1)

        Vector<Point> adjustPoints = new Vector<>();
        for (int d = 0; d < 4; d++) {
            adjustPoints.addElement(new Point(1, 1));
        }
        int lastX = 0, lastY = 0;
        for (int y = 0; y > -4; y--) {
            for (int x = 0; x > -4; x--) {
                if (x != y && ((x + y) % 2 == 0)) {
                    adjustPoints.addElement(new Point(x - lastX, y - lastY));
                    lastX = x;
                    lastY = y;
                }
            }
        }
        Point[] adjusts = new Point[adjustPoints.size()];
        for (int i = 0; i < adjusts.length; i++) {
            adjusts[i] = adjustPoints.elementAt(i);
        }
        return adjusts;
    }

    private DecodeResult decode(QRCodeImage qrCodeImage, Point adjust)
            throws DecodingFailedException {
        try {
            if (numTryDecode == 0) {
                canvas.println("Decoding started");
                int[][] intImage = imageToIntArray(qrCodeImage);
                imageReader = new QRCodeImageReader();
                qrCodeSymbol = imageReader.getQRCodeSymbol(intImage);
            } else {
                canvas.println("--");
                canvas.println("Decoding restarted #" + (numTryDecode));
                qrCodeSymbol = imageReader.getQRCodeSymbolWithAdjustedGrid(adjust);
            }
        } catch (SymbolNotFoundException e) {
            throw new DecodingFailedException(e.getMessage());
        }
        canvas.println("Created QRCode symbol.");
        canvas.println("Reading symbol.");
        canvas.println("Version: " + qrCodeSymbol.getVersionReference());
        canvas.println("Mask pattern: " + qrCodeSymbol.getMaskPatternRefererAsString());
        // blocks contains all (data and RS) blocks in QR Code symbol
        int[] blocks = qrCodeSymbol.getBlocks();
        canvas.println("Correcting data errors.");
        // now blocks turn to data blocks (corrected and extracted from original blocks)
        blocks = correctDataBlocks(blocks);

        try {
            byte[] decodedByteArray =
                    getDecodedByteArray(blocks, qrCodeSymbol.getVersion(), qrCodeSymbol.getNumErrorCollectionCode());
            return new DecodeResult(decodedByteArray, numLastCorrectionFailures);
        } catch (InvalidDataBlockException e) {
            canvas.println(e.getMessage());
            throw new DecodingFailedException(e.getMessage());
        }
    }


    private int[][] imageToIntArray(QRCodeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] intImage = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                intImage[x][y] = image.getPixel(x, y);
            }
        }
        return intImage;
    }

    private int[] correctDataBlocks(int[] blocks) {
        int numSucceededCorrections = 0;
        int numCorrectionFailures = 0;
        int dataCapacity = qrCodeSymbol.getDataCapacity();
        int[] dataBlocks = new int[dataCapacity];
        int numErrorCollectionCode = qrCodeSymbol.getNumErrorCollectionCode();
        int numRSBlocks = qrCodeSymbol.getNumRSBlocks();
        int eccPerRSBlock = numErrorCollectionCode / numRSBlocks;
        if (numRSBlocks == 1) {
            RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
            int ret = corrector.decode(blocks);
            if (ret > 0)
                numSucceededCorrections += ret;
            else if (ret < 0)
                numCorrectionFailures++;
            return blocks;
        } else { //we have to interleave data blocks because symbol has 2 or more RS blocks
            int numLongerRSBlocks = dataCapacity % numRSBlocks;
            int lengthRSBlock = dataCapacity / numRSBlocks;
            if (numLongerRSBlocks == 0) { //symbol has only 1 type of RS block
                int[][] RSBlocks = new int[numRSBlocks][lengthRSBlock];
                //obtain RS blocks
                for (int i = 0; i < numRSBlocks; i++) {
                    for (int j = 0; j < lengthRSBlock; j++) {
                        RSBlocks[i][j] = blocks[j * numRSBlocks + i];
                    }
                    canvas.println("eccPerRSBlock=" + eccPerRSBlock);
                    RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                    int ret = corrector.decode(RSBlocks[i]);
                    if (ret > 0)
                        numSucceededCorrections += ret;
                    else if (ret < 0)
                        numCorrectionFailures++;
                }
                //obtain only data part
                int p = 0;
                for (int i = 0; i < numRSBlocks; i++) {
                    for (int j = 0; j < lengthRSBlock - eccPerRSBlock; j++) {
                        dataBlocks[p++] = RSBlocks[i][j];
                    }
                }
            } else { //symbol has 2 types of RS blocks
                int lengthLongerRSBlock = dataCapacity / numRSBlocks + 1;
                int numShorterRSBlocks = numRSBlocks - numLongerRSBlocks;
                int[][] shorterRSBlocks = new int[numShorterRSBlocks][lengthRSBlock];
                int[][] longerRSBlocks = new int[numLongerRSBlocks][lengthLongerRSBlock];
                for (int i = 0; i < numRSBlocks; i++) {
                    int mod = 0;
                    if (i < numShorterRSBlocks) { //get shorter RS Block(s)
                        for (int j = 0; j < lengthRSBlock; j++) {
                            if (j == lengthRSBlock - eccPerRSBlock) mod = numLongerRSBlocks;
                            shorterRSBlocks[i][j] = blocks[j * numRSBlocks + i + mod];
                        }
                        canvas.println("eccPerRSBlock(shorter)=" + eccPerRSBlock);
                        RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                        int ret = corrector.decode(shorterRSBlocks[i]);
                        if (ret > 0)
                            numSucceededCorrections += ret;
                        else if (ret < 0)
                            numCorrectionFailures++;

                    } else {    //get longer RS Blocks
                        for (int j = 0; j < lengthLongerRSBlock; j++) {
                            if (j == lengthRSBlock - eccPerRSBlock) mod = numShorterRSBlocks;
                            longerRSBlocks[i - numShorterRSBlocks][j] = blocks[j * numRSBlocks + i - mod];
                        }
                        canvas.println("eccPerRSBlock(longer)=" + eccPerRSBlock);
                        RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                        int ret = corrector.decode(longerRSBlocks[i - numShorterRSBlocks]);
                        if (ret > 0)
                            numSucceededCorrections += ret;
                        else if (ret < 0)
                            numCorrectionFailures++;
                    }
                }
                int p = 0;
                for (int i = 0; i < numRSBlocks; i++) {
                    if (i < numShorterRSBlocks) {
                        for (int j = 0; j < lengthRSBlock - eccPerRSBlock; j++) {
                            dataBlocks[p++] = shorterRSBlocks[i][j];
                        }
                    } else {
                        for (int j = 0; j < lengthLongerRSBlock - eccPerRSBlock; j++) {
                            dataBlocks[p++] = longerRSBlocks[i - numShorterRSBlocks][j];
                        }
                    }
                }
            }
            if (numSucceededCorrections > 0)
                canvas.println(numSucceededCorrections + " data errors corrected successfully.");
            else
                canvas.println("No errors found.");
            numLastCorrectionFailures = numCorrectionFailures;
            return dataBlocks;
        }
    }

    private byte[] getDecodedByteArray(int[] blocks, int version, int numErrorCorrectionCode) throws InvalidDataBlockException {
        byte[] byteArray;
        QRCodeDataBlockReader reader = new QRCodeDataBlockReader(blocks, version, numErrorCorrectionCode);
        byteArray = reader.getDataByte();
        return byteArray;
    }

}
