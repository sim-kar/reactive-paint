package se.miun.dt176g.sika2001.reactive;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
    public Oval(Point start, Point end, Color color) {
        super(start, end, color);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color());
        g2.drawOval(topLeft().x(), topLeft().y(), width(), height());
    }
}
