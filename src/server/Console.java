package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;

import tools.Constants;
import tools.Logger;

class Console {
	
	private final Server server;
	private Timer loop;
	
	private JFrame frame;
	private JPanel mainPanel;
	private JPanel menuPanel;
	private JTextArea consoleArea;
	private JPanel serverOverviewPanel;
	private JPanel infoPanel;
	private JPanel commandPanel;
	
	private static final Font TITLE_FONT = new Font("System", Font.BOLD | Font.ITALIC, 25);
	private static final Font TEXT_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 16);
	private static final Font CONSOLE_FONT = new Font("Arial", Font.BOLD, 12);
	
	private static final int OFFSET = 25;
	private static final int UPDATE_DELAY = 100;
	
	private ServerData selectedServer;
	
	private JLabel serverIpLabel;
	private JLabel serverStatusLabel;
	private JLabel serverPriorityLabel;
	private JLabel serverNameLabel;
	
	
	public Console(Server server) {
		this.server = server;
		setupFrame();
		loop();
	}
	
	private void setupFrame() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setSize(1200, 800);
		frame.setTitle("Server Console - " + Constants.NAME);
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
		
		//
		mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		
		//
		menuPanel = new JPanel(new FlowLayout());
		menuPanel.setBackground(new Color(220, 220, 220));
		menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		((FlowLayout) menuPanel.getLayout()).setAlignment(FlowLayout.LEADING);
		frame.add(menuPanel, BorderLayout.NORTH);
		
		{
			JLabel label = new JLabel(Constants.NAME + " | " + Constants.FULL_NAME);
			label.setFont(TITLE_FONT);
			menuPanel.add(label);
		}
		
		//
		serverOverviewPanel = new JPanel(new GridLayout(10, 1));
		serverOverviewPanel.setBackground(new Color(200, 200, 200));
		serverOverviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		mainPanel.add(serverOverviewPanel, BorderLayout.WEST);
		
		{
			for(ServerData server : Constants.SERVERS) {
				JButton button = new JButton("Server " + server.getIp());
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectedServer = server;
					}
				});
				serverOverviewPanel.add(button);
			}
		}
		
		//
		commandPanel = new JPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
		commandPanel.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK), 
				BorderFactory.createEmptyBorder(OFFSET, OFFSET, OFFSET, OFFSET)));
		mainPanel.add(commandPanel, BorderLayout.EAST);
		
		{
			JLabel label = new JLabel("Commands");
			label.setFont(TITLE_FONT);
			commandPanel.add(label);
		}
		
		//
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(OFFSET, OFFSET, OFFSET, OFFSET));
		mainPanel.add(infoPanel, BorderLayout.CENTER);
		
		{
			JLabel label = new JLabel("Data");
			label.setFont(TITLE_FONT);
			infoPanel.add(label);
			
			serverIpLabel = new JLabel("");
			serverIpLabel.setFont(TEXT_FONT);
			infoPanel.add(serverIpLabel);
			
			serverNameLabel = new JLabel("");
			serverNameLabel.setFont(TEXT_FONT);
			infoPanel.add(serverNameLabel);
			
			serverStatusLabel = new JLabel("");
			serverStatusLabel.setFont(TEXT_FONT);
			infoPanel.add(serverStatusLabel);
			
			serverPriorityLabel = new JLabel("");
			serverPriorityLabel.setFont(TEXT_FONT);
			infoPanel.add(serverPriorityLabel);
		}
		
		//
		{
			consoleArea = new JTextArea();
			consoleArea.setFont(CONSOLE_FONT);
			consoleArea.setEditable(false);
			consoleArea.setLineWrap(true);
			consoleArea.setWrapStyleWord(true);
			
			JScrollPane scrollPane = new JScrollPane(consoleArea);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize(new Dimension(0, 200));
			mainPanel.add(scrollPane, BorderLayout.SOUTH);
		}
	}
	
	public void start() {
		frame.setVisible(true);
		consoleArea.requestFocus();
	}
	
	private void updateFrame() {
		SwingUtilities.updateComponentTreeUI(frame);
		/*frame.invalidate();
		frame.validate();
		frame.repaint();*/
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
		updateFrame();
		
		if(!consoleArea.getText().equals(Logger.getLog())) {
			consoleArea.setText(Logger.getLog());
		}
		
		if(selectedServer != null) {
			serverIpLabel.setText("IP: " + selectedServer.getIp());
			serverNameLabel.setText(selectedServer.getName());
			serverStatusLabel.setText("Status: " + (selectedServer.isOnline() ? "online" : "offline"));
			serverPriorityLabel.setText("Priority: Level " + selectedServer.getPriority());
		}
	}
	
	private void exit() {
		loop.stop();
		frame.dispose();
	}
	
}
