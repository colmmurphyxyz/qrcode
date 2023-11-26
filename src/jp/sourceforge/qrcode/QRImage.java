package jp.sourceforge.qrcode;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

public class QRImage implements QRCodeImage {
    BufferedImage image;

    public QRImage(BufferedImage i) {
        this.image = i;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        return image.getRGB(x, y);

    }
}
