package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;

import gui.TextArea;
import logger.Level;
import logger.Logger;
import logger.LoggerListener;
import main.Constants;

class Console {

	private final Server server;
	private Timer loop;

	private JFrame frame;
	private JPanel mainPanel;
	private JPanel menuPanel;
	private TextArea consoleArea;
	private JPanel infoPanel;
	private JPanel commandPanel;

	private static final Font TITLE_FONT = new Font("System", Font.BOLD | Font.ITALIC, 25);
	private static final Font TEXT_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 16);
	private static final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
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
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		frame = new JFrame();
		frame.setSize(1200, 800);
		frame.setTitle("Server Console - " + Constants.NAME);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				server.stop();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		//
		mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);

		//
		{
			menuPanel = new JPanel(new FlowLayout());
			menuPanel.setBackground(new Color(220, 220, 220));
			menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			((FlowLayout) menuPanel.getLayout()).setAlignment(FlowLayout.LEADING);
			frame.add(menuPanel, BorderLayout.NORTH);

			JLabel label = new JLabel(Constants.NAME + " | " + Constants.FULL_NAME);
			label.setFont(TITLE_FONT);
			menuPanel.add(label);
		}

		//
		{
			JPanel serverOverviewPanel = new JPanel();
			serverOverviewPanel.setLayout(new BoxLayout(serverOverviewPanel, BoxLayout.Y_AXIS));
			serverOverviewPanel.setBackground(new Color(200, 200, 200));

			for (ServerData server : Constants.settings.getServers()) {
				JButton button = new JButton("Server " + server.getIp());
				button.setFont(BUTTON_FONT);
				button.setToolTipText("show data");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectedServer = server;
					}
				});
				serverOverviewPanel.add(button);
			}

			JScrollPane scrollPane = new JScrollPane(serverOverviewPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPanel.add(scrollPane, BorderLayout.WEST);
		}

		//
		{
			commandPanel = new JPanel();
			commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));

			JLabel label = new JLabel("Commands");
			label.setFont(TITLE_FONT);
			commandPanel.add(label);
			
			JScrollPane scrollPane = new JScrollPane(commandPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			commandPanel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
					BorderFactory.createEmptyBorder(OFFSET, OFFSET, OFFSET, OFFSET)));
			mainPanel.add(scrollPane, BorderLayout.EAST);
			
			
			JButton button = new JButton("STOP");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					ServerTasks tasks = new ServerTasks();
					tasks.stop();
					try {
						server.send(Context.SERVER_TASKS, selectedServer.getIp(), tasks, 3 * 1000, 5 * 1000);
					} catch (IOException e) {
						Logger.log(e);
					}
				}
			});
			button.setToolTipText("Force Server to stop");
			commandPanel.add(button);
		}

		//
		{
			infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setBorder(BorderFactory.createEmptyBorder(OFFSET, OFFSET, OFFSET, OFFSET));

			JLabel label = new JLabel("Data");
			label.setFont(TITLE_FONT);
			infoPanel.add(label);

			serverNameLabel = new JLabel("");
			serverNameLabel.setFont(TEXT_FONT);
			infoPanel.add(serverNameLabel);

			serverIpLabel = new JLabel("");
			serverIpLabel.setFont(TEXT_FONT);
			infoPanel.add(serverIpLabel);

			serverPriorityLabel = new JLabel("");
			serverPriorityLabel.setFont(TEXT_FONT);
			infoPanel.add(serverPriorityLabel);

			serverStatusLabel = new JLabel("");
			serverStatusLabel.setFont(TEXT_FONT);
			infoPanel.add(serverStatusLabel);

			JScrollPane scrollPane = new JScrollPane(infoPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			mainPanel.add(scrollPane, BorderLayout.CENTER);
		}

		//
		{
			JPanel consolePanel = new JPanel(new BorderLayout());
			consolePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPanel.add(consolePanel, BorderLayout.SOUTH);
			
			{
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				consolePanel.add(panel, BorderLayout.NORTH);
				
				for(Level level : Level.values()) {
					JRadioButton button = new JRadioButton(level.name());
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							level.setDisplay(button.isSelected());
						}
					});
					button.setToolTipText("Display " + level.name().toLowerCase() + " messages");
					button.setSelected(true);
					panel.add(button);
				}
			}
			
			consoleArea = new TextArea();
			consoleArea.setFont(CONSOLE_FONT);
			consoleArea.setEditable(false);
			
			Logger.addListener(new LoggerListener() {
				@Override
				public void onAction(String message, Level level) {
					consoleArea.appendText(message, level.getColor());
					consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
				}
			});

			JScrollPane scrollPane = new JScrollPane(consoleArea);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize(new Dimension(0, 200));
			consolePanel.add(scrollPane, BorderLayout.CENTER);
		}
	}

	public void start() {
		frame.setVisible(true);
		consoleArea.requestFocus();
	}

	@SuppressWarnings("unused")
	private void updateFrame() {
		SwingUtilities.updateComponentTreeUI(frame);
		/*
		 * frame.invalidate(); frame.validate(); frame.repaint();
		 */
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
		if (selectedServer != null) {
			serverIpLabel.setText("IP: " + selectedServer.getIp());
			serverNameLabel.setText("Name: " + selectedServer.getName());
			serverStatusLabel.setText("Status: " + (selectedServer.isOnline() ? "online" : "offline"));
			serverPriorityLabel.setText("Priority: Level " + selectedServer.getPriority());
		}
	}

	public void exit() {
		loop.stop();
		frame.dispose();
	}

}
