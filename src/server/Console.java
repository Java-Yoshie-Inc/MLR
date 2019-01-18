package server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import tools.Logger;

class Console {
	
	private final Server server;
	private Timer loop;
	
	private JFrame frame;
	private JPanel mainPanel;
	
	private JTextArea consoleArea;
	
	private static final int UPDATE_DELAY = 100;
	
	
	public Console(Server server) {
		this.server = server;
		setupFrame();
		loop();
	}
	
	private void setupFrame() {
		frame = new JFrame();
		frame.setSize(1200, 800);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
				exit();
				server.stop();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		
		consoleArea = new JTextArea();
		consoleArea.setFont(new Font("Arial", Font.BOLD, 12));
		consoleArea.setEditable(false);
		consoleArea.setLineWrap(true);
		consoleArea.setWrapStyleWord(true);
		mainPanel.add(consoleArea, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	private void loop() {
		loop = new Timer(UPDATE_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				update();
			}
		});
		loop.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void update() {
		System.out.println("sdfsdf");
		if(!consoleArea.getText().equals(Logger.getLog())) {
			consoleArea.setText(Logger.getLog());
		}
	}
	
	private void exit() {
		loop.stop();
		frame.dispose();
	}
	
}
