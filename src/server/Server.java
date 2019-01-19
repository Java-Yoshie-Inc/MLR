package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import client.User;
import tools.Constants;
import tools.Logger;
import tools.Logger.Level;
import tools.ServerData;
import tools.Tools;

public class Server {
	
	private static final int SERVERS_REACHABILITY_CHECK_DELAY = 15*1000;
	private final int PORT = 2026;
	private final int HTTP_OK_STATUS = 200;
	private final String NAME = "IntexServer";
	
	private Gson gson = new GsonBuilder().create();
	private HttpServer server;
	private Console console;
	private Timer loop;
	
	private boolean hasCheckedReachability = true;
	

	public Server() throws IOException {
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (BindException e) {
			Logger.log("Server is already running on this ip");
			return;
		}
		server.setExecutor(null);

		server.createContext(Constants.UPDATE_CONTEXT, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				InputStream in = arg.getRequestBody();
				User user = gson.fromJson(readInputStream(in), User.class);

				Logger.log(user.getName() + " updates");

				ServerResponse serverResponse = new ServerResponse(NAME);
				String gsonString = gson.toJson(serverResponse, ServerResponse.class);
				arg.sendResponseHeaders(HTTP_OK_STATUS, gsonString.length());
				OutputStream output = arg.getResponseBody();
				output.write(gsonString.getBytes());
				output.close();
			}
		});
		
		server.createContext(Constants.REACHABILITY_CHECK_CONTEXT, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				arg.getRequestBody();
				String response  = "";
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
	}
	
	public void start() {
		server.start();
		console = new Console(this);
		Logger.log("Server started");
		loop();
	}
	
	public void stop() {
		loop.stop();
		server.stop(0);
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.start();
	}

	private void loop() {
		loop = new Timer(SERVERS_REACHABILITY_CHECK_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(!hasCheckedReachability) return;
				
				new Thread(new Runnable() {
					public void run() {
						try {
							checkServerReachability();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		loop.setInitialDelay(0);
		loop.start();
	}
	
	private void checkServerReachability() throws IOException {
		try {
			if(!Tools.hasInternet()) {
				Logger.log("No internet connection", Level.WARNING);
				return;
			}
			
			hasCheckedReachability = false;
			
			for(ServerData server : Constants.SERVERS) {
				Logger.log("Checking status of Server " + server);
				
				try {
					String url = "http://" + server.getIp() + Constants.REACHABILITY_CHECK_CONTEXT;
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
					Logger.log("Server " + server + " is online");
				} catch (ConnectException | FileNotFoundException e) {
					server.setOnline(false);
					Logger.log("Server " + server + " is down", Level.WARNING);
				}
			}
		} catch (Exception e) {
			Logger.log(e.getMessage(), Level.ERROR);
		}
		
		hasCheckedReachability = true;
	}

	private String readInputStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}
		reader.close();

		return response.toString();
	}

}
