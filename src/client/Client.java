package client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import main.Component;
import main.Constants;
import server.Context;
import server.ServerData;
import server.ServerResponse;

public class Client extends Component {
	
	private final User USER;
	
	private static final int UPDATE_DELAY = 500;
	private static final int SERVER_REACHABILITY_CHECK_DELAY = 20*1000;
	
	public static void main(String[] args) throws IOException {
		new Client();
	}
	
	public Client() {
		USER = new User();
		checkServerReachability();
		loop();
	}
	
	private void loop() {
		Timer loop = new Timer(UPDATE_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				update();
			}
		});
		loop.start();
		
		Timer loop2 = new Timer(SERVER_REACHABILITY_CHECK_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				checkServerReachability();
			}
		});
		loop2.setInitialDelay(0);
		loop2.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void checkServerReachability() {
		for(ServerData server : Constants.settings.getServers()) {
			try {
				Client.super.send(Context.REACHABILITY_CHECK, server.getIp(), "", 3000, 5000);
				server.setOnline(true);
			} catch (SocketTimeoutException | ConnectException e) {
				server.setOnline(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void update() {
		ServerData currentServer = getCurrentServer();
		if(currentServer == null) return;
		System.out.println("Connecting to Server " + currentServer.getIp());
		
		try {
			String gsonResponse = super.send(Context.UPDATE, currentServer.getIp(), USER, 3000, 5000);
			ServerResponse response = gson.fromJson(gsonResponse, ServerResponse.class);
			System.out.println("Response: " + response.getServer());
		} catch (SocketTimeoutException | ConnectException e) {
			currentServer.setOnline(false);
			update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ServerData getCurrentServer() {
		List<ServerData> servers = Arrays.asList(Constants.settings.getServers());
		Collections.shuffle(servers);
		for(ServerData s : servers) {
			if(s.isOnline()) {
				return s;
			}
		}
		System.out.println("All Servers all down ");
		return null;
	}
	
}