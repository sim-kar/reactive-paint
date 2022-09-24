package se.miun.dt176g.sika2001.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/* COMPONENTS */

	private static final DrawingPanel DRAWING_PANEL = new DrawingPanel();
	private static final int MIN_THICKNESS = 1;
	private static final int MAX_THICKNESS = 50;
	private static final int DEFAULT_THICKNESS = 3;
	private static final int THICKNESS_TICK_SPACING = 1;
	private final JPanel toolPanel;
	private Color color;
	private int thickness;
	private Tool tool;

	public MainFrame() {
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String header = "Reactive Paint";
		this.setTitle(header);

		this.setLayout(new BorderLayout());

		// set default values
		this.color = Color.BLACK;
		this.thickness = DEFAULT_THICKNESS;
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
		JSlider thicknessSlider = new JSlider(MIN_THICKNESS, MAX_THICKNESS, DEFAULT_THICKNESS);
		thicknessSlider.setOrientation(SwingConstants.VERTICAL);
		thicknessSlider.setMajorTickSpacing(THICKNESS_TICK_SPACING);
		thicknessSlider.setSnapToTicks(true);
		thicknessSlider.setPaintTicks(true);
		thicknessSlider.addChangeListener(e -> thickness = thicknessSlider.getValue());

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

	/* BEHAVIOR */

	Disposable shapes = getMousePressedAndReleased().subscribe();

	/**
	 * Get an Observable that adds {@link Shape}s to the drawing panel when the mouse is pressed
	 * and released. A {@link Point} is recorded when the mouse is pressed, and another is recorded
	 * when the mouse is released. The two points are used to draw shapes with a width and a height,
	 * such as rectangles.
	 *
	 * @return an Observable that emits when the mouse is pressed and released.
	 */
	private Observable<MouseEvent> getMousePressedAndReleased() {
		return Observable.create(emitter ->
			DRAWING_PANEL.addMouseListener(new MouseAdapter() {
				Point press;
				Point release;

				@Override
				public void mousePressed(MouseEvent e) {
					press = new Point(e.getX(), e.getY());
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					release = new Point(e.getX(), e.getY());

	/**
	 * Get an Observable that emits the value of the provided slider whenever it is changed.
	 *
	 * @param slider the slider to monitor.
	 * @return the Observable.
	 */
	private Observable<Integer> getSliderValue(JSlider slider) {
		return Observable.create(emitter ->
				slider.addChangeListener(event ->
						emitter.onNext(slider.getValue()))
		);
	}

	/**
	 * Get an Observable that emits true whenever the provided button is clicked.
	 *
	 * @param button the button to monitor.
	 * @return the Observable.
	 */
	private Observable<Boolean> getButtonClick(JButton button) {
		return Observable.create(emitter ->
			button.addActionListener(event -> emitter.onNext(true))
		);
	}
}
