package client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
	private ServerData currentServer = Constants.SERVERS[0];
	
	public static void main(String[] args) throws IOException {
		new Client();
	}
	
	public Client() {
		USER = new User();
		loop();
	}
	
	private void loop() {
		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					update();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		timer.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void update() throws IOException {
		try {
			String url = "http://" + Constants.SERVER_IPS[0] + Constants.PORT + "/update";
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
			this.currentServer.setOnline(false);
			
			for(ServerData server : Constants.SERVERS) {
				if(currentServer.isOnline()) {
					this.currentServer = server;
					update();
					break;
				}
			}
		}
	}
	
}