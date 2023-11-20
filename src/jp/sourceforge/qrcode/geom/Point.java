/*
 * created: 2004/09/13
 */
package jp.sourceforge.qrcode.geom;

import jp.sourceforge.qrcode.util.QRCodeUtility;


/**
 * Class to represent a Point in 2D space
 */
public class Point {
    public static final int RIGHT = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int TOP = 8;

    int x;
    int y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Translate the point by dx units along the x-axis and dy along the y-axis
     * @param dx number of units to translate along the x-axis
     * @param dy number of units to translate along the y-axis
     */
    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * String representation of the Point
     * @return String representation of the Point
     */
    public String toString() {
        return "(" + x + "," + y + ")";
    }

/*	public static Point getBarycenter(Point p1, Point p2, float ratio) {
		return new Point(
			(int)(p1.getX() + ( p2.getX() - p1.getX() ) * ratio),
			(int)(p1.getY() + ( p2.getY() - p1.getY() ) * ratio)
			);
	}*/


    /**
     * Get the midpoint along the straight line that connects p1 and p2
     * @param p1 Point 1
     * @param p2 Point 2
     * @return Point p such that dist(p, p1) == dist(p, p2)
     */
    public static Point getCenter(Point p1, Point p2) {
        return new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    }

    /**
     * Compares two points, and returns true if they are equal, false otherwise
     * @param compare Point to compare this point to
     * @return true, if the two points have identical x and y values, false otherwise
     */
    public boolean equals(Point compare) {
        return (x != compare.x || y != compare.y);
    }

    /**
     * Compute the Euclidean distance between two points using Pythagoras' Theorem
     * @param other Second point to measure the distance to
     * @return integer approximation og the distance between two points
     */
    public int distanceOf(Point other) {
        int x2 = other.getX();
        int y2 = other.getY();
        return QRCodeUtility.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
    }
}
