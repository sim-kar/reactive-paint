package com.sim_kar.reactive_paint;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1>
 * A Canvas-object for displaying all graphics already drawn.
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

	private Drawing drawing;

	/**
	 * Constructs a new DrawingPanel.
	 */
	public DrawingPanel() {
		drawing = new Drawing();
	}

	/**
	 * Redraw this DrawingPanel. Will redraw the set {@link Drawing}.
	 */
	public void redraw() {
		repaint();
	}

	/**
	 * Set a {@link Drawing} for this panel to display.
	 *
	 * @param d the Drawing to display
	 */
	public void setDrawing(Drawing d) {
		drawing = d;
		repaint();
	}

	/**
	 * Get the {@link Drawing} that this panel is displaying.
	 *
	 * @return the currently displayed drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		drawing.draw(g);
	}

}
