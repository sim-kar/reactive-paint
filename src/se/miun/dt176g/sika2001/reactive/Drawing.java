package se.miun.dt176g.sika2001.reactive;


import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


/**
 * <h1>Drawing</h1> 
 * Comprises a number of {@link Shape}s.
 */
public class Drawing implements Drawable {
	private final List<Shape> shapes;

	/**
	 * Construct a new drawing.
	 */
	public Drawing() {
		this.shapes = new ArrayList<>();
	}

	/**
	 * Add a shape to the drawing.
	 * 
	 * @param s the {@link Shape} to add
	 */
	public void addShape(Shape s) {
		this.shapes.add(s);
	}

	/**
	 * Remove all {@link Shape}s from this drawing.
	 */
	public void clear() {
		this.shapes.clear();
	}

	@Override
	public void draw(Graphics g) {
		shapes.forEach(s -> s.draw(g));
	}
}
