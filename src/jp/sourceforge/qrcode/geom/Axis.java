package jp.sourceforge.qrcode.geom;

import jp.sourceforge.qrcode.reader.QRCodeImageReader;

/**
 * This class is designed to move a target point based on independent axes.
 * It allows the movement of target coordinates on a rotated, scaled, and gauche QR Code image.
 */

public class Axis {

    int sin, cos;
    int modulePitch;
    Point origin;

    /**
     * Creates a new Axis object with the specified angle and module pitch.
     * @param angle The angle value is an array of two integers (sin and cos).
     * @param modulePitch Module pitch or size.
     */

    public Axis(int[] angle, int modulePitch) {
        this.sin = angle[0];
        this.cos = angle[1];
        this.modulePitch = modulePitch;
        this.origin = new Point();
    }

    /**
     * Sets the origin point for the axis.
     * @param origin Point object representing the origin point.
     */

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    /**
     * Sets the module pitch for the axis.
     * @param modulePitch Module pitch or size.
     */

    public void setModulePitch(int modulePitch) {
        this.modulePitch = modulePitch;
    }

    /**
     * Translates a point by the specified offset.
     * @param offset Point object representing the offset.
     * @return Translated point.
     */
    public Point translate(Point offset) {
        int moveX = offset.getX();
        int moveY = offset.getY();
        return this.translate(moveX, moveY);
    }

    /**
     * Translates a point by the specified offset relative to the given origin.
     * @param origin Point object representing the origin point.
     * @param offset Point object representing the offset.
     * @return Translated point.
     */
    public Point translate(Point origin, Point offset) {
        setOrigin(origin);
        int moveX = offset.getX();
        int moveY = offset.getY();
        return this.translate(moveX, moveY);
    }

    /**
     * Translates a point by the specified offset relative to the given origin.
     * @param origin Point object representing the origin point.
     * @param moveX Horizontal offset.
     * @param moveY Vertical offset.
     * @return Translated point.
     */
    public Point translate(Point origin, int moveX, int moveY) {
        setOrigin(origin);
        return this.translate(moveX, moveY);
    }

    /**
     * Translates a point by the specified offset relative to the given origin and module pitch.
     * @param origin Point object representing the origin point.
     * @param modulePitch Module pitch or size.
     * @param moveX Horizontal offset.
     * @param moveY Vertical offset.
     * @return Translated point.
     */
    public Point translate(Point origin, int modulePitch, int moveX, int moveY) {
        setOrigin(origin);
        this.modulePitch = modulePitch;
        return this.translate(moveX, moveY);
    }

    /**
     * Translates a point by the specified horizontal and vertical offsets.
     * @param moveX Horizontal offset.
     * @param moveY Vertical offset.
     * @return Translated point.
     */

    //maeda's FIX
    public Point translate(int moveX, int moveY) {
        long dp = QRCodeImageReader.DECIMAL_POINT;
        Point point = new Point();
        int dx = (moveX == 0) ? 0 : (modulePitch * moveX) >> dp;
        int dy = (moveY == 0) ? 0 : (modulePitch * moveY) >> dp;
        point.translate((dx * cos - dy * sin) >> dp, (dx * sin + dy * cos) >> dp);
        point.translate(origin.getX(), origin.getY());
        //System.out.println((modulePitch * moveX) >> dp);

        return point;
    }

    //older translate
/*	public Point translate(int moveX, int moveY) {
		long dp = QRCodeImageReader.DECIMAL_POINT;
		Point point = new Point(0,0);
		point.translate(origin.getX(), origin.getY());

		int yf = 0; //, xf = 0 ?
		if (moveX >= 0 & moveY >= 0) yf = 1;
		else if (moveX < 0 & moveY >= 0) yf = -1;
		else if (moveX >= 0 & moveY < 0) yf = -1;
		else if (moveX < 0 & moveY < 0) yf = 1;
		//System.out.println((modulePitch * moveX) >> dp);
		int dx = (moveX == 0) ? 0 : (modulePitch * moveX) >> dp;
		int dy = (moveY == 0) ? 0 : (modulePitch * moveY) >> dp;
		if (dx != 0 && dy != 0)
			point.translate((dx * cos - dy * sin) >> dp, yf * (dx * cos + dy * sin) >> dp);
		else if (dy == 0) {
			if (dx < 0) yf = -yf;
			point.translate((dx * cos) >> dp, yf * (dx * sin) >> dp);
		}
		else if (dx == 0) {
			if (dy < 0) yf = -yf;
			point.translate(-yf * (dy * sin) >> dp, (dy * cos) >> dp);
		}

		return point;
	}

*/
}
