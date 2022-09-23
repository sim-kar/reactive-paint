package se.miun.dt176g.sika2001.reactive;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <h1>Oval</h1>
 * A straight line between the given start and end {@link Point}s.
 */
public class Line extends Shape {

    /**
     * Create a new line running between the given {@link Point}s start and  end.
     *
     * @param start starting {@link Point} of the line.
     * @param end ending {@link Point} of the line.
     */
    public Line(Point start, Point end, Color color) {
        super(start, end, color);
    }

    @Override
    void drawShape(Graphics g) {
        g.drawLine(start().x(), start().y(), end().x(), end().y());
    }
}
