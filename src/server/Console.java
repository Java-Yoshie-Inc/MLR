package server;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Console {
	
	private JFrame frame;
	private JPanel mainPanel;
	
	public Console() {
		setupFrame();
	}
	
	private void setupFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel(new BorderLayout());
	}
	
}
