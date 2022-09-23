package se.miun.dt176g.sika2001.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/* COMPONENTS */

	private static final DrawingPanel DRAWING_PANEL = new DrawingPanel();;
	private final JPanel toolPanel;
	private Color color;
	private Tool tool;

	public MainFrame() {
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String header = "Reactive Paint";
		this.setTitle(header);

		this.setLayout(new BorderLayout());

		// set default values
		this.color = Color.BLACK;
		this.tool = Tool.FREEHAND;

		DRAWING_PANEL.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(DRAWING_PANEL, BorderLayout.CENTER);

		toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));

		JButton freehandButton = new JButton("Freehand");
		freehandButton.addActionListener(e -> this.tool = Tool.FREEHAND);
		JButton lineButton = new JButton("Line");
		lineButton.addActionListener(e -> this.tool = Tool.LINE);
		JButton rectangleButton = new JButton("Rectangle");
		rectangleButton.addActionListener(e -> this.tool = Tool.RECTANGLE);
		JButton ovalButton = new JButton("Oval");
		ovalButton.addActionListener(e -> this.tool = Tool.OVAL);
		JButton clearButton = new JButton("Clear");
		JButton colorButton = new JButton("Color");
		colorButton.addActionListener((e) ->
			this.color = JColorChooser.showDialog(
					toolPanel,
					"Choose Color",
					this.color
			)
		);
		JSlider thicknessSlider = new JSlider();
		thicknessSlider.setOrientation(SwingConstants.VERTICAL);

		// center the thickness slider horizontally
		JPanel thicknessSliderPanel = new JPanel();
		thicknessSliderPanel.setLayout(new FlowLayout());
		thicknessSliderPanel.add(thicknessSlider);

		toolPanel.add(freehandButton);
		toolPanel.add(lineButton);
		toolPanel.add(rectangleButton);
		toolPanel.add(ovalButton);
		toolPanel.add(colorButton);
		toolPanel.add(clearButton);
		toolPanel.add(thicknessSliderPanel);

		this.getContentPane().add(toolPanel, BorderLayout.WEST);

		this.setJMenuBar(new Menu(this));
	}
}
