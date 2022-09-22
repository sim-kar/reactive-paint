package se.miun.dt176g.sika2001.reactive;

import javax.swing.SwingUtilities;


/**
* <h1>AppStart</h1>
*
* @author  --YOUR NAME HERE--
* @version 1.0
* @since   2022-09-08
*/
public class AppStart {

	public static void main(String[] args) {
		
		// Make sure GUI is created on the event dispatching thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}