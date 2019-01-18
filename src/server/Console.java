package server;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Console {
	
	private final Server server;
	
	private JFrame frame;
	private JPanel mainPanel;
	
	
	public Console(Server server) {
		this.server = server;
		setupFrame();
	}
	
	private void setupFrame() {
		frame = new JFrame();
		frame.setSize(1200, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				server.stop();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		
		frame.setVisible(true);
	}
	
}
