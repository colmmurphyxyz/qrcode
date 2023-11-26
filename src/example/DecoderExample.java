package example;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.QRImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DecoderExample {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("please pass a QR Code image filepath as an argument");
            System.err.println("e.g java -jar qrcode.jar qrcode.png");
        }
        QRCodeDecoder decoder = new QRCodeDecoder();
        QRCodeDecoder.setDebugOutput(System.err);
        for (String filename : args) {
            try {
                BufferedImage image;
                if (filename.matches("https?://.*")) {
                    image = ImageIO.read(new URL(filename));
                } else {
                    image = ImageIO.read(new File(filename));
                }
                byte[] result = decoder.decodeImage(new QRImage(image));
                String decodedText = new String(result);
                System.out.println(decodedText);
            } catch (IOException e) {
                System.err.println("ERROR: Couldn't read " + filename);
            }
        }
    }
}

