package jp.sourceforge.qrcode.util;

import jp.sourceforge.qrcode.geom.Line;
import jp.sourceforge.qrcode.geom.Point;

/*
 * This class must be an "edition independent" class for debug information controll.
 * I think it's good idea to modify this class with a adapter pattern
 */
public class DebugCanvasAdapter implements DebugCanvas {

    @Override
    public void println(String string) {
    }
    @Override
    public void drawPoint(Point point, Color color) {
    }
    @Override
    public void drawCross(Point point, Color color) {
    }
    @Override
    public void drawPoints(Point[] points, Color color) {
    }
    @Override
    public void drawLine(Line line, Color color) {
    }
    @Override
    public void drawLines(Line[] lines, Color color) {
    }
    @Override
    public void drawPolygon(Point[] points, Color color) {
    }
    @Override
    public void drawMatrix(boolean[][] matrix) {
    }
}

