package se.miun.dt176g.sika2001.reactive;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class. Derived classes must implement {@link Shape#drawShape}.
 */
public abstract class Shape implements Drawable {

    private final Point start;
    private final Point end;
    private final int thickness;
    private final Color color;

    public Shape(Point start, Point end, int thickness, Color color) {
        this.start = start;
        this.end = end;
        this.thickness = thickness;
        this.color = color;
    }

    public Point start() { return start; }
    public Point end() { return end; }
    public int thickness() { return thickness; }
    public Color color() { return color; }

    public int width() { return Math.abs(start.x() - end.x()); }
    public int height() { return Math.abs(start.y() - end.y()); }
    public int left() { return Math.min(start.x(), end.x()); }
    public int right() { return Math.max(start.x(), end.x()); }
    public int top() { return Math.min(start.y(), end.y()); }
    public int bottom() { return Math.max(start.y(), end.y()); }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(thickness()));
        g2.setColor(color());
        drawShape(g);
    }

    /**
     * Used by concrete child classes to draw their {@link Shape}.
     * Uses the parent's graphics object to draw with the set color and thickness.
     *
     * @param g the graphics object that will draw the shape.
     */
    abstract void drawShape(Graphics g);
}
