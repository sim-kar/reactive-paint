package se.miun.dt176g.sika2001.reactive;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <h1>Rectangle</h1>
 * Creates a Rectangle with one corner at the given start {@link Point} and the opposite corner at
 * the end {@link Point}.
 */
public class Rectangle extends Shape {

	/**
	 * Create a new rectangle with opposing corners at the given {@link Point}s.
	 *
	 * @param start position of the first corner.
	 * @param end position of the second corner (opposite the first).
	 */
	public Rectangle(Point start, Point end, Color color) {
		super(start, end, color);
	}

	@Override
	void drawShape(Graphics g) {
		g.drawRect(left(), top(), width(), height());
	}
}
