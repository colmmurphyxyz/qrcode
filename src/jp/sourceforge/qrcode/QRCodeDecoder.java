package jp.sourceforge.qrcode;

import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.data.QRCodeSymbol;
import jp.sourceforge.qrcode.ecc.RsDecode;
import jp.sourceforge.qrcode.exception.DecodingFailedException;
import jp.sourceforge.qrcode.exception.InvalidDataBlockException;
import jp.sourceforge.qrcode.exception.SymbolNotFoundException;
import jp.sourceforge.qrcode.geom.Point;
import jp.sourceforge.qrcode.reader.QRCodeDataBlockReader;
import jp.sourceforge.qrcode.reader.QRCodeImageReader;
import jp.sourceforge.qrcode.util.DebugCanvas;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Vector;

public class QRCodeDecoder {

    QRCodeSymbol qrCodeSymbol;
    int[][] intImage;
    QRCodeImageReader imageReader;

    int numDecodeAttempts;
    Vector<DecodeResult> results;
    int numLastCorrectionFailures;

    static DebugCanvas canvas = new DebugOutput(System.err);

    public static DebugCanvas getDebugCanvas() {
        return QRCodeDecoder.canvas;
    }

    public static PrintStream getDebugOutput() {
        return QRCodeDecoder.canvas.ps;
    }

    public static void setDebugOutput(PrintStream ps) {
        QRCodeDecoder.canvas.setDebugOutput(ps);
    }

    public static DebugCanvas getCanvas() {
        return QRCodeDecoder.canvas;
    }

    public static void setCanvas(DebugCanvas canvas) {
        QRCodeDecoder.canvas = canvas;
    }

    public QRCodeDecoder() {
        this.numDecodeAttempts = 0;
        this.results = new Vector<>();
    }

    public byte[] decodeImage(URL imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(imageUrl);
        return decodeImage(new QRImage(image));
    }

    public byte[] decodeImage(QRCodeImage qrCodeImage) throws DecodingFailedException {
        Point[] adjusts = getAdjustPoints();
        Vector<DecodeResult> results = new Vector<>();

        intImage = imageToIntArray(qrCodeImage);
        imageReader = new QRCodeImageReader();
        qrCodeSymbol = imageReader.getQRCodeSymbol((intImage));
        System.out.println("Decoding started");
        for (Point adjust : adjusts) {
            DecodeResult result = decode(qrCodeImage, adjust);
            if (result.isCorrectionSucceeded()) {
                return result.getDecodedBytes();
            } else {
                // decoding succeeses, but could not correct errors
                results.add(result);
                // retry...
            }
        }
        // if no attempts to decode and error correct succeeded, then decoding failed
        if (results.isEmpty()) {
            throw new DecodingFailedException("Image unrecognizable, decoding failed");
        }

        DecodeResult bestCorrectionAttempt = results.get(0);
        for (DecodeResult r : results) {
            if (r.getNumCorrectionFailures() < bestCorrectionAttempt.getNumCorrectionFailures()) {
                bestCorrectionAttempt = r;
            }
        }
        // return the best attempt at decoding,
        // i.e the attempt with the least number of correction failures
        return bestCorrectionAttempt.getDecodedBytes();
    }

    private DecodeResult decode(QRCodeImage qrCodeImage, Point adjust) {
        try {
            qrCodeSymbol = imageReader.getQRCodeSymbolWithAdjustedGrid(adjust);
        } catch (SymbolNotFoundException e) {
            throw new DecodingFailedException(e.getMessage());
        }
        int[] blocks = qrCodeSymbol.getBlocks();
        blocks = correctDataBlocks(blocks);
        try {
            byte[] decodedByteArray = getDecodedByteArray(
                    blocks,
                    qrCodeSymbol.getVersion(),
                    qrCodeSymbol.getNumErrorCollectionCode());
            return new DecodeResult(decodedByteArray, numLastCorrectionFailures);
        } catch (InvalidDataBlockException e) {
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

    private int[] correctDataBlocks(int[] blocks) {
        int numSucceededCorrections = 0;
        int numCorrectionFailures = 0;
        int dataCapacity = qrCodeSymbol.getDataCapacity();
        int[] dataBlocks = new int[dataCapacity];
        int numErrorCollectionCode = qrCodeSymbol.getNumErrorCollectionCode();;
        int numRSBlocks = qrCodeSymbol.getNumRSBlocks();
        int eccPerRSBlock = numErrorCollectionCode / numRSBlocks;

        if (numRSBlocks == 1) {
            RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
            corrector.decode(blocks);
            return blocks;
        } // else
        int numLongerRSBlocks = dataCapacity % numRSBlocks;
        int lengthRSBlock = dataCapacity / numRSBlocks;
        if (numLongerRSBlocks == 0) {
            int[][] RSBlocks = new int[numRSBlocks][lengthRSBlock];
            for (int i = 0; i < numRSBlocks; i++) {
                for (int j = 0; j < lengthRSBlock; j++) {
                    RSBlocks[i][j] = blocks[j * numRSBlocks + i];
                }
                RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                int ret = corrector.decode(RSBlocks[i]);
                if (ret > 0) {
                    numSucceededCorrections += ret;
                } else if (ret < 0) {
                    numCorrectionFailures++;
                }
            }
            int p = 0;
            for (int i = 0; i < numRSBlocks; i++) {
                for (int j = 0; j < lengthRSBlock - eccPerRSBlock; j++) {
                    dataBlocks[p++] = RSBlocks[i][j];
                }
            }
        } else {
            int lengthLongerRSBlock = dataCapacity / numRSBlocks + 1;
            int numShorterRSBlocks = numRSBlocks - numLongerRSBlocks;
            int[][] shorterRSBlocks = new int[numShorterRSBlocks][lengthRSBlock];
            int[][] longerRSBlocks = new int[numLongerRSBlocks][lengthLongerRSBlock];

            for (int i = 0; i < numRSBlocks; i++) {
                int mod = 0;
                if (i < numShorterRSBlocks) { //get shorter RS Block(s)
                    for (int j = 0; j < lengthRSBlock; j++) {
                        if (j == lengthRSBlock - eccPerRSBlock) {
                            mod = numLongerRSBlocks;
                        }
                        shorterRSBlocks[i][j] = blocks[j * numRSBlocks + i + mod];
                    }
                    RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                    int ret = corrector.decode(shorterRSBlocks[i]);
                    if (ret > 0)
                        numSucceededCorrections += ret;
                    else if (ret < 0)
                        numCorrectionFailures++;

                } else {    //get longer RS Blocks
                    for (int j = 0; j < lengthLongerRSBlock; j++) {
                        if (j == lengthRSBlock - eccPerRSBlock) {
                            mod = numShorterRSBlocks;
                        }
                        longerRSBlocks[i - numShorterRSBlocks][j] = blocks[j * numRSBlocks + i - mod];
                    }
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
        numLastCorrectionFailures = numCorrectionFailures;
        return dataBlocks;
    }

    private byte[] getDecodedByteArray(int[] blocks, int version, int numErrorCorrectionCode) {
        QRCodeDataBlockReader reader = new QRCodeDataBlockReader(blocks, version, numErrorCorrectionCode);
        return reader.getDataByte();
    }
}

