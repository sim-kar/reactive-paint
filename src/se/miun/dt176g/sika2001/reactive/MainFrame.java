package se.miun.dt176g.sika2001.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
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
	private Observable<Server> server;
	private final Observable<Shape> drawShapes;

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

		drawShapes = drawShapes()
				.replay()
				.autoConnect();

		Disposable addShapes = drawShapes.subscribe(this::addShapeToDrawing);
	}

	/**
	 * Start hosting a server that others can connect to using the same port number. All shapes
	 * drawn by the host and connected clients will be shared.
	 *
	 * @throws IOException if an I/O error occurs when opening the server socket
	 */
	public void host() throws IOException {
		startServer();
		ConnectableObservable<Client> clients = getClients(server).publish();

		Observable<Shape> clientShapes = getShapesFromClients(clients)
				// handle error here since it will be stifled by retry otherwise
				.doOnError(e -> displayError(e, "Error communicating with client:"))
				.retry()
				.replay()
				.autoConnect();

		Disposable receiveShapes = clientShapes.subscribe(this::addShapeToDrawing);

		Observable<Shape> allShapes = Observable.merge(drawShapes, clientShapes);

		Disposable sendShapes = sendShapesToClients(clients, allShapes).subscribe();

		Disposable startHosting = clients.connect();
	}

	/**
	 * Join a server that is hosted at the given port. Drawn shapes will be shared among the host
	 * and all connected clients.
	 *
	 * @param port the host's port
	 * @throws IOException if an I/O error occurs when connecting to the host
	 */
	public void join(int port) throws IOException {
		ConnectableObservable<Client> client = getClientConnectedToHost(port).publish();

		Disposable sendShapes = sendShapesToHost(client, drawShapes).subscribe(
				__ -> {},
				e -> displayError(e, "Error sending shape to host:")
		);

		Disposable receiveShapes = getShapesFromHost(client).subscribe(
				this::addShapeToDrawing,
				e -> displayError(e, "Error receiving shape from host:")
		);

		Disposable connectToHost = client.connect();
	}

	/**
	 * Get an Observable with the port number that this MainFrame is hosting at.
	 * Will return an empty Observable if not hosting.
	 *
	 * @return the observable
	 */
	public Observable<Integer> getPort() {
		if (server != null) {
			return server.map(Server::getPort);
		}

		return Observable.empty();
	}

	/**
	 * Add a shape to the drawing. It will be drawn on Swing's Event Dispatch Thread.
	 *
	 * @param shape the shape to add
	 */
	private void addShapeToDrawing(Shape shape) {
		EventQueue.invokeLater(() -> {
			DRAWING_PANEL.getDrawing().addShape(shape);
			DRAWING_PANEL.redraw();
		});
	}

	/**
	 * Starts a new server, if one isn't started already. To avoid blocking the EDT, the server's
	 * observable is subscribed to the I/O scheduler. The observable is hot, and will autoconnect.
	 */
	private void startServer() {
		if (server == null) {
			// use fromCallable so that server initialization doesn't block EDT
			// multicast server, otherwise a new server will be emitted by the callable every time
			server = Observable.fromCallable(Server::new)
					.subscribeOn(Schedulers.io())
					.publish()
					.autoConnect(1);
		}
	}

	/**
	 * Get an observable of all {@link Client}s connected to the given {@link Server}.
	 *
	 * @param listeningServer an observable with the server listening for clients
	 * @return the Observable
	 */
	private Observable<Client> getClients(Observable<Server> listeningServer) {
		return listeningServer.map(Server::start)
				.flatMap(socket -> socket.map(Client::new)
						.subscribeOn(Schedulers.io())
				);
	}

	/**
	 * Get an observable of shapes received from clients connected to the host. The Observable is
	 * subscribed on the I/O scheduler.
	 *
	 * @param clients an observable of connected clients
	 * @return the observable
	 */
	private Observable<Shape> getShapesFromClients(ConnectableObservable<Client> clients) {
		return clients.flatMap(client -> Observable.<Shape>create(
				emitter -> {
					// keep getting shapes from client
					// readObject blocks, so use io scheduler
					while (!client.isShutdown()) {
						try {
							emitter.onNext((Shape) client.read());
						} catch (SocketException e) {
							// if we propagate the error when a client disconnects, the observable
							// getting shapes from clients will restart, meaning all clients will be
							// disconnected. We avoid that by handling the error here instead
							displayError(e, "Client disconnected:");
							client.shutdown();
						} catch (Exception e) {
							emitter.onError(e);
							client.shutdown();
						}
					}
					emitter.onComplete();
				}).subscribeOn(Schedulers.io())
		);
	}

	/**
	 * An observable that sends the given shapes to the given clients. The observable is subscribed
	 * to the I/O scheduler.
	 *
	 * @param clients the clients to send shapes to
	 * @param shapes the shapes to send
	 * @return the observable
	 */
	private Observable<Shape> sendShapesToClients(ConnectableObservable<Client> clients,
												  Observable<Shape> shapes) {
		return clients.flatMap(c -> Observable.just(c)
				.subscribeOn(Schedulers.io())
				.repeat()
				.zipWith(shapes, (client, shape) -> {
					if (client.isShutdown()) return shape;

					try {
						client.write(shape);
					} catch (Exception e) {
						// shut down if the client has disconnected
						client.shutdown();
						throw e;
					}

					return shape;
				})
		);
	}

	/**
	 * Get a client that is connected to the host at the given port, which can be used to send
	 * and receive shapes to the host. The observable is subscribed to the I/O scheduler.
	 *
	 * @param port the port of the host to connect to
	 * @return the observable
	 */
	private Observable<Client> getClientConnectedToHost(int port) {
		return Observable.just(port)
				.subscribeOn(Schedulers.io())
				.map(p -> new Socket("localhost", p))
				.map(Client::new);
	}

	/**
	 * An observable that sends the given shapes to the host the given client is connected to. The
	 * observable is subscribed to the I/O scheduler.
	 *
	 * @param client the client connected to the host to send the shapes to
	 * @param shapes the shapes to send
	 * @return the observable
	 */
	private Observable<Shape> sendShapesToHost(ConnectableObservable<Client> client,
											   Observable<Shape> shapes) {
		return client.flatMap(c -> shapes
				.map(s -> {
					try {
						c.write(s);
					} catch (Exception e) {
						c.shutdown();
						throw e;
					}
					return s;
				})
				.subscribeOn(Schedulers.io())
		);
	}

	/**
	 * Get an observable of shapes received from the host the given client is connected to. The
	 * observable is subscribed to the I/O scheduler.
	 *
	 * @param client the client connected to the host to receive shapes from
	 * @return the observable
	 */
	private Observable<Shape> getShapesFromHost(ConnectableObservable<Client> client) {
		return client.flatMap(c -> Observable.<Shape>create(
				emitter -> {
					// keep getting shapes from server
					while (!c.isShutdown()) {
						try {
							emitter.onNext((Shape) c.read());
						} catch (Exception e) {
							emitter.onError(e);
							c.shutdown();
						}
					}
					emitter.onComplete();
				}).subscribeOn(Schedulers.io())
		);
	}

	/**
	 * Display a message dialog with the given message, and the message from the given Throwable.
	 *
	 * @param e the Throwable
	 * @param message the message to display
	 */
	private void displayError(Throwable e, String message) {
		JOptionPane.showMessageDialog(
				this,
				message + "\n" + e.getMessage()
		);
	}

	/**
	 * Get an Observable that emits drawn shapes. The emitted shapes depend on the selected tool,
	 * color and thickness.
	 *
	 * @return the Observable of the shapes
	 */
	private Observable<Shape> drawShapes() {
		// Get the events of the mouse being pressed and dragged until the button is released,
		// and use the coordinates from the events to create a new shapes
		return getMousePressedEvent().mergeWith(getMouseDraggedEvent())
				.map(e -> new Point(e.getX(), e.getY()))
				.buffer(getMouseReleasedEvent())
				.map(this::createShape);
	}

	/**
	 * Create a new {@link Shape} from the given {@link Point}s. The concrete shape that is created
	 * is determined by the currently selected tool.
	 *
	 * @param points the points to create the shape from
	 * @return the shape
	 */
	private Shape createShape(List<Point> points) {
		Point start = points.get(0);
		Point end = points.get(points.size() - 1);

		if (tool == Tool.LINE) return new Line(start, end, thickness, color);
		if (tool == Tool.OVAL ) return new Oval(start, end, thickness, color);
		if (tool == Tool.RECTANGLE) return new Rectangle(start, end, thickness, color);

		FreehandLine freehandLine = new FreehandLine(start, end, thickness, color);
		points.forEach(freehandLine::addPoint);
		return freehandLine;
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
