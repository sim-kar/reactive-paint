package se.miun.dt176g.sika2001.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private final DrawingPanel drawingPanel;
	private final JPanel toolPanel;
	private Color color;

	public MainFrame() {
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String header = "Reactive Paint";
		this.setTitle(header);

		this.setLayout(new BorderLayout());

		this.color = Color.BLACK;

		drawingPanel = new DrawingPanel();
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));

		JButton freehandButton = new JButton("Freehand");
		JButton lineButton = new JButton("Line");
		JButton rectangleButton = new JButton("Rectangle");
		JButton ovalButton = new JButton("Oval");
		JButton colorButton = new JButton("Color");
		colorButton.addActionListener((e) ->
			this.color = JColorChooser.showDialog(
					toolPanel,
					"Choose Color",
					this.color
			)
		);
		JButton clearButton = new JButton("Clear");
		JSlider thicknessSlider = new JSlider();
		thicknessSlider.setOrientation(SwingConstants.VERTICAL);
		thicknessSlider.setAlignmentX(SwingConstants.CENTER);

		toolPanel.add(freehandButton);
		toolPanel.add(lineButton);
		toolPanel.add(rectangleButton);
		toolPanel.add(ovalButton);
		toolPanel.add(colorButton);
		toolPanel.add(clearButton);

		// center the thickness slider horizontally
		JPanel thicknessSliderPanel = new JPanel();
		thicknessSliderPanel.setLayout(new FlowLayout());
		thicknessSliderPanel.add(thicknessSlider);
		toolPanel.add(thicknessSliderPanel);

		this.getContentPane().add(toolPanel, BorderLayout.WEST);

		this.setJMenuBar(new Menu(this));
	}
}
