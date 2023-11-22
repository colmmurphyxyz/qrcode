package jp.sourceforge.qrcode.util;

public class QRCodeUtility {

    public static int sqrt(int val) {
        try {
            return (int) Math.sqrt(val);
        } catch (ArithmeticException e) {
            // If Math.sqrt is not available, use the original implementation
            return originalSqrt(val);
        }
    }

    private static int originalSqrt(int val) {
        int temp, g = 0, b = 0x8000, bshft = 15;
        do {
            if (val >= (temp = (((g << 1) + b) << bshft--))) {
                g += b;
                val -= temp;
            }
        } while ((b >>= 1) > 0);

        return g;
    }
}
