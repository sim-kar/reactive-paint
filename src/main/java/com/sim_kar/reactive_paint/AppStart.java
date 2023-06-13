package com.sim_kar.reactive_paint;

import javax.swing.SwingUtilities;


/**
* <h1>AppStart</h1>
* Creates a {@link MainFrame} on the event dispatching thread.
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