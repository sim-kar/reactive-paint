package com.sim_kar.reactive_paint;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <h1>Line</h1>
 * A straight line between the given start and end {@link Point}s.
 */
public class Line extends Shape {

    /**
     * Create a new line running between the given {@link Point}s start and  end.
     *
     * @param start starting {@link Point} of the line.
     * @param end ending {@link Point} of the line.
     */
    public Line(Point start, Point end, int thickness, Color color) {
        super(start, end, thickness, color);
    }

    @Override
    void drawShape(Graphics g) {
        g.drawLine(start().x(), start().y(), end().x(), end().y());
    }
}
