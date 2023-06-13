package com.sim_kar.reactive_paint;

/**
 * <h1>Drawable</h1>
 * Something that can be drawn. Must implement {@link Drawable#draw(java.awt.Graphics)}.
 */

@FunctionalInterface
interface Drawable {
	/**
	 * Draw this drawable using the supplied {@link java.awt.Graphics} object.
	 *
	 * @param g the graphics object to use to draw
	 */
	void draw(java.awt.Graphics g);
}
