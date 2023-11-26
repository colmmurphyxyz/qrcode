package jp.sourceforge.qrcode.util;


/**
 * Changed the interface to enum
 */
public enum Color {
    GRAY(0xAAAAAA),
    LIGHTGRAY(0xBBBBBB),
    DARKGRAY(0x444444),
    BLACK(0x000000),
    WHITE(0xFFFFFF),
    BLUE(0x8888FF),
    GREEN(0x88FF88),
    LIGHTBLUE(0xBBBBFF),
    LIGHTGREEN(0xBBFFBB),
    RED(0xFF88888),
    ORANGE(0xFFFF88);

    private final int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
