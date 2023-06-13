package com.sim_kar.reactive_paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Freehand Line</h1>
 * A contiguous line between the given start and end {@link Point}s. The path from the start point
 * to the end point can be altered by adding points (in order of traversal) with
 * {@link FreehandLine#addPoint(Point)}.
 */
public class FreehandLine extends Shape {

    private final List<Point> points;

    /**
     * Create a new line running between the given {@link Point}s start and  end.
     *
     * @param start starting {@link Point} of the line.
     * @param end ending {@link Point} of the line.
     */
    public FreehandLine(Point start, Point end, int thickness, Color color) {
        super(start, end, thickness, color);
        this.points = new ArrayList<>();
    }

    @Override
    void drawShape(Graphics g) {
        Path2D path = new Path2D.Float();
        path.moveTo(start().x(), start().y());
        points.forEach(p -> path.lineTo(p.x(), p.y()));
        path.moveTo(end().x(), end().y());

        Graphics2D g2 = (Graphics2D) g;
        g2.draw(path);
    }

    /**
     * Add a {@link Point} that the freehand line will move to next.
     *
     * @param point the point to move to next.
     */
    public void addPoint(Point point) {
        this.points.add(point);
    }
}
