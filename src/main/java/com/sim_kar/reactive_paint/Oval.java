package com.sim_kar.reactive_paint;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <h1>Oval</h1>
 * An oval with the height and width of a bounding box with one corner at the given start
 * {@link Point} and the opposite corner at the end {@link Point}.
 */
public class Oval extends Shape {

    /**
     * Create a new oval spanning the height and width of the bounding box created by the
     * {@link Point}s start and end.
     *
     * @param start position of the first corner of the bounding box.
     * @param end position of the second corner of the bounding box (opposite the first).
     */
    public Oval(Point start, Point end, int thickness, Color color) {
        super(start, end, thickness, color);
    }

    @Override
    void drawShape(Graphics g) {
        g.drawOval(left(), top(), width(), height());
    }
}
