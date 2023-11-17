package jp.sourceforge.qrcode.util;

/*
 * This class must be modified as a adapter class for "edition dependent" methods
 */

public class QRCodeUtility {
    // Because CLDC1.0 does not support Math.sqrt(), we have to define it manually.
    // faster sqrt (GuoQing Hu's FIX)
    public static int sqrt(int val) {
//		using estimate method from http://www.azillionmonkeys.com/qed/sqroot.html 
//		System.out.print(val + ", " + (int)Math.sqrt(val) + ", "); 
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
