package se.miun.dt176g.sika2001.reactive;


import java.awt.Color;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 */
public abstract class Shape implements Drawable {

    private final Point start;
    private final Point end;
    private final Color color;

    public Shape(Point start, Point end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Point start() { return start; }
    public Point end() { return end; }
    public Color color() { return color; }

    public int width() { return Math.abs(start.x() - end.x()); }
    public int height() { return Math.abs(start.y() - end.y()); }
    public int left() { return Math.min(start.x(), end.x()); }
    public int right() { return Math.max(start.x(), end.x()); }
    public int top() { return Math.min(start.y(), end.y()); }
    public int bottom() { return Math.max(start.y(), end.y()); }
}
