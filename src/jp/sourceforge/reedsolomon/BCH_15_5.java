package jp.sourceforge.reedsolomon;

/**
 * <a href="http://sourceforge.jp/projects/reedsolomon/">link to reedsolomon</a>
 */
public final class BCH_15_5 {
    private static final int GX = 0x137;
    private static final BCH_15_5 instance = new BCH_15_5();
    private final int[] trueCodes = new int[32];

    private BCH_15_5() {
        makeTrueCodes();
    }

    public static BCH_15_5 getInstance() {
        return instance;
    }

    /**
     * 正規のコード・テーブルの作成
     */
    private void makeTrueCodes() {
        for (int i = 0; i < trueCodes.length; i++) {
            trueCodes[i] = slowEncode(i);
        }
    }

    private int slowEncode(int data) {
        int wk = 0;
        data <<= 5;
        for (int i = 0; i < 5; i++) {
            wk <<= 1;
            data <<= 1;
            if (((wk ^ data) & 0x400) != 0) {
                wk ^= GX;
            }
        }
        return (data & 0x7c00) | (wk & 0x3ff);
    }

    public int encode(int data) {
        return trueCodes[data & 0x1f];
    }

    /**
     * calculate the <a hreh="https://www.omnicalculator.com/other/hamming-distance#what-is-the-hamming-distance">Hamming Distance</a> between two 32-bit integers
     * the Hamming distance in this case being the number of bits that differ between the two inputs
     * e.g calcHammingDistance(0b11010, 0b11111) = 2
     * @param c1 int
     * @param c2 int
     * @return The Hamming Distance between the two binary sequences
     */
    private static int calcHammingDistance(int c1, int c2) {
        int hammingDistance = 0;
        // c1 XOR c2 outputs a sequence where a given digit is 1 if the two inputs have different values
        // at that index
        int bitDiffs = c1 ^ c2;
        while (bitDiffs != 0) {
            if ((bitDiffs & 1) != 0) {
                hammingDistance++;
            }
            bitDiffs >>= 1;
        }
        return hammingDistance;
    }

    /**
     * BCH(15, 5)符号のデコード
     *
     * @param data int
     *             入力データ
     * @return int
     * -1 : エラー訂正不能
     * >= 0 : 訂正データ
     */
    public int decode(int data) {
        data &= 0x7fff;
        /*
         * 最小符号間距離が7であるので、ハミング距離が3以下の正規のコードを探す
         * エラー訂正と検出の組み合わせは、以下の通り
         *		訂正	検出
         *		  3
         *		  2		  4
         *		  1		  5
         */
        for (int i = 0; i < trueCodes.length; i++) {
            int code = trueCodes[i];
            if (calcHammingDistance(data, code) <= 3) {
                return code;
            }
        }
        return -1;
    }
}
