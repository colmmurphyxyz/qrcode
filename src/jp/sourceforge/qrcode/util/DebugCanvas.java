package jp.sourceforge.qrcode.util;

import jp.sourceforge.qrcode.geom.Line;
import jp.sourceforge.qrcode.geom.Point;

/*
 * This class must be an "edition independent" class for debug information controls.
 * I think it's good idea to modify this class with a adapter pattern
 */
public interface DebugCanvas {
    void println(String string);

    void drawPoint(Point point, Color color);

    void drawCross(Point point, Color color);

    void drawPoints(Point[] points, Color color);

    void drawLine(Line line, Color color);

    void drawLines(Line[] lines, Color color);

    void drawPolygon(Point[] points, Color color);

    void drawMatrix(boolean[][] matrix);
}

