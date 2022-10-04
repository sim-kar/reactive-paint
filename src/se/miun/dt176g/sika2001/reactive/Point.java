package se.miun.dt176g.sika2001.reactive;

/**
 * <h1>Point</h1> 
 * A point on a 2D plane with an x- and a y-coordinate.
 */
public class Point {

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
		Point p = (Point) o;
		return (x == p.x() && y == p.y());
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}

}
