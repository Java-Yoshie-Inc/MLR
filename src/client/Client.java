package client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import server.ServerResponse;
import tools.Constants;
import tools.ServerData;

public class Client {
	
	private final Gson gson = new GsonBuilder().create();
	private final User USER;
	
	public static void main(String[] args) throws IOException {
		new Client();
	}
	
	public Client() {
		USER = new User();
		checkServerReachability();
		loop();
	}
	
	private void loop() {System.out.println("sfdsf");
		Timer loop = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				update();
			}
		});
		loop.start();
		
		Timer loop2 = new Timer(60*1000, new ActionListener() {
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
						String url = "http://" + server.getIp() + Constants.PORT + Constants.REACHABILITY_CHECK_CONTEXT;
						URL obj = new URL(url);
						HttpURLConnection con = (HttpURLConnection) obj.openConnection();
						
						con.setRequestMethod("POST");
						con.setDoOutput(true);
						con.setDoInput(true);
						
						OutputStream output = con.getOutputStream();
						output.write("".getBytes());
						output.close();
						
						con.getInputStream();
						
						server.setOnline(true);
					} catch (ConnectException | FileNotFoundException e) {
						server.setOnline(false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	private void update() {
		ServerData currentServer = getCurrentServer();
		
		if(currentServer == null) {
			System.out.println("All Servers are down");
			return;
		}
		
		System.out.println(currentServer.getIp());
		
		try {
			String url = "http://" + currentServer.getIp() + Constants.PORT + Constants.UPDATE_CONTEXT;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			
			OutputStream output = con.getOutputStream();
			output.write(gson.toJson(USER, USER.getClass()).getBytes());
			output.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuffer gsonResponse = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				gsonResponse.append(inputLine);
			}
			in.close();
			
			ServerResponse response = gson.fromJson(gsonResponse.toString(), ServerResponse.class);
			System.out.println(response.getName());
		} catch (Exception e) {
			currentServer.setOnline(false);
			update();
		}
	}
	
	private ServerData getCurrentServer() {
		ServerData server = null;
		for(ServerData s : Constants.SERVERS) {
			if(s.isOnline()) {
				if(server == null || s.getPriority() < server.getPriority()) {
					server = s;
				}
			}
		}
		return server;
	}
	
}