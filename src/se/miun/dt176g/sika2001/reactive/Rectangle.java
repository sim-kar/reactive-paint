package se.miun.dt176g.sika2001.reactive;

import java.awt.Graphics;
import java.awt.Graphics2D;

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
	public Rectangle(Point start, Point end) {
		super(start, end);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawRect(this.topLeft().x(), this.topLeft().y(), this.width(), this.height());
	}
}
