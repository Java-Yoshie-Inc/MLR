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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.Component;
import server.ServerData;
import server.ServerResponse;
import tools.Constants;
import tools.Context;

public class Client extends Component {
	
	private final Gson gson = new GsonBuilder().create();
	private final User USER;
	
	private static final int UPDATE_DELAY = 500;
	private static final int SERVER_REACHABILITY_CHECK_DELAY = 22*1000;
	
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
		for(ServerData server : Constants.SERVERS) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Client.super.send(Context.REACHABILITY_CHECK, server.getIp(), "");
						server.setOnline(true);
					} catch (SocketTimeoutException | ConnectException e) {
						server.setOnline(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	private void update() {
		ServerData currentServer = getCurrentServer();
		if(currentServer == null) return;
		System.out.println("Connecting to Server " + currentServer.getIp());
		
		try {
			String gsonResponse = super.send(Context.UPDATE, currentServer.getIp(), USER);
			ServerResponse response = gson.fromJson(gsonResponse, ServerResponse.class);
			System.out.println("Response: " + response.getName());
		} catch (SocketTimeoutException | ConnectException e) {
			currentServer.setOnline(false);
			update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ServerData getCurrentServer() {
		List<ServerData> servers = Arrays.asList(Constants.SERVERS);
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