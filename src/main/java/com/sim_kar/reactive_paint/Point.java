package com.sim_kar.reactive_paint;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>Point</h1> 
 * A point on a 2D plane with an x- and a y-coordinate.
 */
public class Point implements Serializable {

	@Serial
	private static final long serialVersionUID = -1167382948560506726L;
	private int x, y;

	/**
	 * Contracts a new Point with the given coordinates.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x-coordinate of this Point.
	 *
	 * @return the x-coordinate
	 */
	public int x() {
		return x;
	}

	/**
	 * Get the y-coordinate of this Point.
	 *
	 * @return the y-coordinate.
	 */
	public int y() {
		return y;
	}

	/**
	 * Set a new x-coordinate for this Point.
	 *
	 * @param x the new x-coordinate.
	 */
	public void x(int x) {
		this.x = x;
	}

	/**
	 * Set a new y-coordinate for this Point.
	 *
	 * @param y the new y-coordinate
	 */
	public void y(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof Point)) return false;
		Point p = (Point) o;
		return (x == p.x() && y == p.y());
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}

}
