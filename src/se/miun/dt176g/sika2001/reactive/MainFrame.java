package se.miun.dt176g.sika2001.reactive;

import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * Contains a drawing panel for drawing different shapes, a tool panel for selecting tools and
 * parameters such as color, and a menu bar.
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
	private Server server;
	private Observable<Shape> drawShapes;

	/**
	 * Constructs a new MainFrame.
	 */
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
		DRAWING_PANEL.setBackground(Color.WHITE);

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

		// Get the events of the mouse being pressed and dragged until the button is released,
		// and use the coordinates from the events to create a new shapes depending on selected
		// tool.
		drawShapes = getMousePressedEvent().mergeWith(getMouseDraggedEvent())
				.map(e -> new Point(e.getX(), e.getY()))
				.buffer(getMouseReleasedEvent())
				.map(l -> {
					Point start = l.get(0);
					Point end = l.get(l.size() - 1);

					if (tool == Tool.LINE) return new Line(start, end, thickness, color);
					if (tool == Tool.OVAL ) return new Oval(start, end, thickness, color);
					if (tool == Tool.RECTANGLE) return new Rectangle(start, end, thickness, color);

					FreehandLine freehandLine = new FreehandLine(start, end, thickness, color);
					l.forEach(freehandLine::addPoint);
					return freehandLine;
				})
				.replay()
				.autoConnect();

				Disposable addShapes = drawShapes.subscribe(s -> {
					DRAWING_PANEL.getDrawing().addShape(s);
					DRAWING_PANEL.redraw();
				});
	}

	/**
	 * Start hosting a server that others can connect to using the same port number.
	 *
	 * @throws IOException if an I/O error occurs when opening the server socket
	 */
	public void host() throws IOException {
		server = new Server();

		ConnectableObservable<Client> clients = server.start()
				// avoid blocking UI thread when clients connect to server
				.subscribeOn(Schedulers.io())
				.map(Client::new)
				.publish();

		Observable<Shape> drawClientShapes = clients
				.map(socket -> socket.getInput())
						.subscribeOn(Schedulers.io())
						.doOnNext(__ -> System.out.println("host receiving on " + Thread.currentThread()))
						.flatMap(stream -> Observable.<Shape>create(
								emitter -> {
									// keep getting shapes from client
									// readObject blocks, so use io scheduler
									while (true) {
										try {
											emitter.onNext((Shape) stream.readObject());
										} catch (Exception e) {
											emitter.onError(e);
											JOptionPane.showMessageDialog(
													this,
													"Error reading shape from client\n"
															+ e.getMessage()
											);
										}
									}
								}).doOnError(e -> System.out.println("Error: " + e.getMessage()))
								.subscribeOn(Schedulers.io()) // FIXME: needed?
				).doOnError(e -> System.out.println("Error: " + e.getMessage()))
				.replay()
				.autoConnect();

		Observable<Shape> allShapes = Observable.merge(drawShapes, drawClientShapes);

		allShapes
				.distinct()
				.subscribe(s -> {
					EventQueue.invokeLater(() -> {
						System.out.println("Drawing shape on" + Thread.currentThread());
						DRAWING_PANEL.getDrawing().addShape(s);
						DRAWING_PANEL.redraw();
					});
				});

		clients
				.flatMap(s -> Observable.just(s.getOutput())
				.subscribeOn(Schedulers.io())
				.repeat()
				.zipWith(allShapes, (out, shape) -> {
					System.out.println("host sending shape to client on thread " + Thread.currentThread());
					out.writeObject(shape);
					return shape;
				})
				.doOnNext(shape -> System.out.println("host sending " + shape.toString() + " on " + Thread.currentThread())
				)
		).subscribe(s -> System.out.println("Subscribe: Send shapes to clients"));

		clients.connect();
	}

	public void join(int port) throws IOException {
		Socket client = new Socket("localhost", port);
		ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());

		Disposable sendShapes = drawShapes
				.doOnNext(s -> System.out.println("client sending " + s.toString()))
				.doFinally(() -> System.out.println("Client finished!"))
				.subscribe(output::writeObject);

		Disposable receiveShapes = Observable.just(new ObjectInputStream(client.getInputStream()))
				.subscribeOn(Schedulers.io())
				.doOnNext(s -> System.out.println("client receiving input on " + Thread.currentThread()))
				.flatMap(s -> Observable.<Shape>create(
								emitter -> {
									// keep getting shapes from server
									while (true) {
										emitter.onNext((Shape) s.readObject());
									}
								}).subscribeOn(Schedulers.io())
				).subscribe(s -> {
					EventQueue.invokeLater(() -> {
						System.out.println(Thread.currentThread());
						DRAWING_PANEL.getDrawing().addShape(s);
						DRAWING_PANEL.redraw();
					});
				});
	}

	/**
	 * Get the server if this MainFrame is hosting. Will return null otherwise.
	 *
	 * @return the server, or null if not hosting
	 */
	public @Nullable Server getServer() {
		return server;
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
