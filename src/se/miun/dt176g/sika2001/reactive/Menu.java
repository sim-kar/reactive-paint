package se.miun.dt176g.sika2001.reactive;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.io.IOException;


/**
 * <h1>Menu</h1> 
 * A menu that can be used to trigger events in the supplied {@link MainFrame}.
 */
public class Menu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new Menu that can trigger events in the given {@link MainFrame}.
	 *
	 * @param frame the MainFrame that this menu is connected to.
	 */
	public Menu(MainFrame frame) {
		init(frame);
	}

	private void init(MainFrame frame) {

		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Connect");
		this.add(menu);


		menuItem = new JMenuItem("Host");
		menuItem.addActionListener(e -> host(frame));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Join");
		menuItem.addActionListener(e ->  join(frame));
		menu.add(menuItem);
	}

	/**
	 * Instruct the given frame to start hosting. Will also add the port the frame is hosting on
	 * to the frame's title.
	 *
	 * @param frame the frame to use as host
	 */
	private void host(MainFrame frame) {
		try {
			frame.host();

			frame.getPort().subscribe(port -> {
				frame.setTitle(frame.getTitle() + " [Hosting on port " + port + "]");
				JOptionPane.showMessageDialog(frame, "Success!\nHosting on port " + port);
			});
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unable to start host server.");
		}
	}

	/**
	 * Instruct the given frame to connect to a host as a client. Will show an input dialog where
	 * the user must enter the port of a host. A connection will only be established if the entered
	 * port corresponds to an open socket; otherwise an error dialog will be shown.
	 *
	 * @param frame the client frame
	 */
	private void join(MainFrame frame) {
		String input = JOptionPane.showInputDialog(frame, "Select a port to connect to.");

		try {
			int port = Integer.parseInt(input);

			frame.join(port);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "The port needs to be a number.");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unable to connect to that port.");
		}
	}

}
