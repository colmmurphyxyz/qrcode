package jp.sourceforge.qrcode;

import jp.sourceforge.qrcode.geom.Line;
import jp.sourceforge.qrcode.geom.Point;
import jp.sourceforge.qrcode.util.DebugCanvas;

import java.io.PrintStream;

public class DebugOutput implements DebugCanvas {
    public PrintStream ps = System.out;

    public DebugOutput() {

    }

    public DebugOutput(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public PrintStream getDebugOutput() {
        return this.ps;
    }

    @Override
    public void setDebugOutput(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public void println(String string) {
        ps.println(string);
    }

    @Override
    public void drawPoint(Point point, int color) {

    }

    @Override
    public void drawCross(Point point, int color) {

    }

    @Override
    public void drawPoints(Point[] points, int color) {

    }

    @Override
    public void drawLine(Line line, int color) {

    }

    @Override
    public void drawLines(Line[] lines, int color) {

    }

    @Override
    public void drawPolygon(Point[] points, int color) {

    }

    @Override
    public void drawMatrix(boolean[][] matrix) {

    }
}
