package se.miun.dt176g.sika2001.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

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
		/* COMPONENTS */

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

		this.toolPanel = new JPanel();
		this.toolPanel.setLayout(new BoxLayout(this.toolPanel, BoxLayout.Y_AXIS));

		JButton freehandButton = new JButton("Freehand");
		JButton lineButton = new JButton("Line");
		JButton rectangleButton = new JButton("Rectangle");
		JButton ovalButton = new JButton("Oval");
		JButton clearButton = new JButton("Clear");
		JButton colorButton = new JButton("Color");

		JSlider thicknessSlider = new JSlider(MIN_THICKNESS, MAX_THICKNESS, DEFAULT_THICKNESS);
		thicknessSlider.setOrientation(SwingConstants.VERTICAL);
		thicknessSlider.setMajorTickSpacing(THICKNESS_TICK_SPACING);
		thicknessSlider.setSnapToTicks(true);
		thicknessSlider.setPaintTicks(true);

		// center the thickness slider horizontally
		JPanel thicknessSliderPanel = new JPanel();
		thicknessSliderPanel.setLayout(new FlowLayout());
		thicknessSliderPanel.add(thicknessSlider);

		this.toolPanel.add(freehandButton);
		this.toolPanel.add(lineButton);
		this.toolPanel.add(rectangleButton);
		this.toolPanel.add(ovalButton);
		this.toolPanel.add(colorButton);
		this.toolPanel.add(clearButton);
		this.toolPanel.add(thicknessSliderPanel);

		this.getContentPane().add(this.toolPanel, BorderLayout.WEST);

		this.setJMenuBar(new Menu(this));

		/* BEHAVIOR */

		Disposable selectFreehandTool = getButtonClick(freehandButton)
				.subscribe(e -> this.tool = Tool.FREEHAND);
		Disposable selectLineTool = getButtonClick(lineButton)
				.subscribe(e -> this.tool = Tool.LINE);
		Disposable selectRectangleTool = getButtonClick(rectangleButton)
				.subscribe(e -> this.tool = Tool.RECTANGLE);
		Disposable selectOvalTool = getButtonClick(ovalButton)
				.subscribe(e -> this.tool = Tool.OVAL);

		Disposable showColorDialog = getButtonClick(colorButton).subscribe(e ->
				this.color = JColorChooser.showDialog(toolPanel,"Choose Color", this.color)
		);

		Disposable clear = getButtonClick(clearButton).subscribe(e -> {
			DRAWING_PANEL.getDrawing().clear();
			DRAWING_PANEL.redraw();
		});

		Disposable setThickness = getSliderValue(thicknessSlider)
				.subscribe(i -> this.thickness = i);

		// Get the events of the mouse being pressed and released, and use the coordinates from
		// the events to create new shapes. The currently selected tool determines what kind of
		// shape is created.
		Disposable drawShapes = Observable.zip(
				getMousePressedEvent(),
				getMouseReleasedEvent(),
				Arrays::asList
		).subscribe(l -> {
			Point press = new Point(l.get(0).getX(), l.get(0).getY());
			Point release = new Point(l.get(1).getX(), l.get(1).getY());

			switch (tool) {
				case FREEHAND -> { return; }
				case LINE -> DRAWING_PANEL.getDrawing()
						.addShape(new Line(press, release, thickness, color));
				case OVAL -> DRAWING_PANEL.getDrawing()
						.addShape(new Oval(press, release, thickness, color));
				case RECTANGLE -> DRAWING_PANEL.getDrawing()
						.addShape(new Rectangle(press, release, thickness, color));
			}

			DRAWING_PANEL.redraw();
		});

		// Get the events of the mouse being pressed and dragged until the button is released,
		// and use the coordinates from the events to create a new freehand line.
		Disposable drawFreehand = getMousePressedEvent().mergeWith(getMouseDraggedEvent())
				.buffer(getMouseReleasedEvent())
				.subscribe(l -> {
					if (this.tool != Tool.FREEHAND) return;

					Point start = new Point(
							l.get(0).getX(),
							l.get(0).getY()
					);
					Point end = new Point(
							l.get(l.size() - 1).getX(),
							l.get(l.size() - 1).getY()
					);

					FreehandLine line = new FreehandLine(start, end, thickness, color);

					l.forEach(event -> line.addPoint(new Point(event.getX(), event.getY())));

					DRAWING_PANEL.getDrawing().addShape(line);
					DRAWING_PANEL.redraw();
				});
	}

	/**
	 * Get and Observable that emits a MouseEvent every time the mouse is pressed.
	 *
	 * @return the Observable.
	 */
	private Observable<MouseEvent> getMousePressedEvent() {
		return Observable.create(emitter ->
				DRAWING_PANEL.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent event) {
						emitter.onNext(event);
					}
				})
		);
	}

	/**
	 * Get and Observable that emits a MouseEvent every time the mouse is released.
	 *
	 * @return the Observable.
	 */
	private Observable<MouseEvent> getMouseReleasedEvent() {
		return Observable.create(emitter ->
				DRAWING_PANEL.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent event) {
						emitter.onNext(event);
					}
				})
		);
	}

	/**
	 * Get and Observable that emits a MouseEvent when the mouse is dragged.
	 *
	 * @return the Observable.
	 */
	private Observable<MouseEvent> getMouseDraggedEvent() {
		return Observable.create(emitter ->
				DRAWING_PANEL.addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent event) {
						emitter.onNext(event);
					}
				})
		);
	}

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
