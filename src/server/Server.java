package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import client.User;
import main.Component;
import tools.Constants;
import tools.Context;
import tools.Logger;
import tools.Logger.Level;
import tools.Tools;


public class Server extends Component {
	
	private static final Random random = new Random();
	
	private static final int SERVERS_REACHABILITY_CHECK_DELAY = 15*1000;
	private final int PORT = 2026;
	private final int HTTP_OK_STATUS = 200;
	private final long ID = random.nextLong();
	
	private ServerData data;
	private HttpServer server;
	private Console console;
	private Timer loop;
	private List<User> users = new ArrayList<User>();
	
	private boolean hasCheckedReachability = true;
	
	
	public Server() throws IOException {
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (BindException e) {
			JOptionPane.showMessageDialog(null, "Server is already running on this ip");
			return;
		}
		server.setExecutor(null);
		
		server.createContext(Context.UPDATE, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				InputStream in = arg.getRequestBody();
				String input = readInputStream(in);
				ServerData server = getPreferredServer();
				
				if(server != null) {
					if(server.equals(Server.this.data)) {
						User user = gson.fromJson(input, User.class);
						
						login(user);
						Logger.log(user.getName() + " updates");
					} else {
						Logger.log("Some Server wants to update -> Server gets transferred");
						Server.super.send(Context.UPDATE, server.getIp(), input);
					}
				}
				
				ServerResponse serverResponse = new ServerResponse("ServerName");
				String gsonString = gson.toJson(serverResponse, ServerResponse.class);
				arg.sendResponseHeaders(HTTP_OK_STATUS, gsonString.length());
				OutputStream output = arg.getResponseBody();
				output.write(gsonString.getBytes());
				output.close();
			}
		});
		
		server.createContext(Context.REACHABILITY_CHECK, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				arg.getRequestBody();
				String response  = "";
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
		
		server.createContext(Context.SERVER_IDENTIFY, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				ServerData server = gson.fromJson(readInputStream(arg.getRequestBody()), ServerData.class);
				Logger.log(server.getIp() + " tries to identify");
				
				String response  = gson.toJson(Server.this.ID, long.class);
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
	}
	
	private void login(User user) {
		if(!users.contains(user)) {
			users.add(user);
			Logger.log(user.getName() + " logged in");
		}
	}
	
	
	/**
	 * Returns server with highest priority which is online
	 */
	private ServerData getPreferredServer() {
		ServerData server = null;
		for(ServerData s : Constants.SERVERS) {
			if(s.isOnline()) {
				if(server == null || s.getPriority() < server.getPriority()) {
					server = s;
				}
			}
		}
		if(server == null) System.out.println("All Servers all down ");
		return server;
	}
	
	
	/**
	 * Server tries to find its ip
	 */
	private void identify() {
		for(ServerData server : Constants.SERVERS) {
			try {
				String gsonResponse = super.send(Context.SERVER_IDENTIFY, server.getIp(), server);
				long response = gson.fromJson(gsonResponse, long.class);
				if(response == this.ID) {
					this.data = server;
					break;
				}
			} catch (SocketTimeoutException e) {
				Logger.log("Server " + server + " is down", Level.WARNING);
			} catch (Exception e) {
				Logger.log(e);
			}
		}
		
		if(this.data == null) {
			Logger.log("Couldnt identify", Level.WARNING);
		} else {
			Logger.log("Identified as " + data);
		}
	}
	
	public void start() {
		server.start();
		this.console = new Console(this);
		Logger.log("Server started");
		identify();
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
							Logger.log(e);
						}
						checkClientConnection();
					}
				}).start();
			}
		});
		loop.setInitialDelay(0);
		loop.start();
	}
	
	/**
	 * Checks whether client disconnected
	 */
	private void checkClientConnection() {
		
	}
	
	/**
	 * Checks which server is online
	 */
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
					super.send(Context.REACHABILITY_CHECK, server.getIp(), "");
					server.setOnline(true);
					Logger.log("Server " + server + " is online");
				} catch (SocketTimeoutException e) {
					server.setOnline(false);
					Logger.log("Server " + server + " is down", Level.WARNING);
				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		
		hasCheckedReachability = true;
	}
	
	/**
	 * Converts InputStream to String
	 */
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
