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
		
		menuItem = new JMenuItem("Some menu item 2");
		menuItem.addActionListener(e ->  anotherEvent(frame));
		menu.add(menuItem);
	}

	private void host(MainFrame frame) {
		try {
			frame.host();

			// will throw exception if hosting fails, so should not be null
			int port = frame.getServer().getPort();

			String title = frame.getTitle() + " [Hosting on port " + port + "]";
			frame.setTitle(title);
			JOptionPane.showMessageDialog(frame, "Success!\nHosting on port " + port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unable to start host server.");
		}
	}
	
	private void anotherEvent(MainFrame frame) {
		
	}

}
