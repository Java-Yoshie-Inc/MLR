package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import client.ClientData;
import client.User;
import logger.Level;
import logger.Logger;
import main.Component;
import main.Constants;
import tools.Encoder;
import tools.FileSaver;
import tools.Stopwatch;
import tools.Tools;

public class Server extends Component {
	
	private static final int SERVERS_REACHABILITY_CHECK_DELAY = 30 * 1000;
	private static final int SYNCHRONIZATION_DELAY = 15 * 1000;
	private static final int HTTP_OK_STATUS = 200;
	private static final int CLIENT_LOGOUT_TIME = 5 * 1000;
	private final long ID = random.nextLong();
	
	private ServerData data;
	private HttpServer server;
	private Console console;
	private List<ClientData> clients = new ArrayList<ClientData>();
	private FileSaver[] synchronizedFiles = new FileSaver[0];
	
	private boolean hasCheckedReachability = true;
	
	public Server() throws IOException {
		try {
			server = HttpServer.create(new InetSocketAddress(Constants.settings.getPort()), 0);
		} catch (BindException e) {
			JOptionPane.showMessageDialog(null, "Server is already running on this ip");
			stop();
			return;
		}
		server.setExecutor(null);
		
		server.createContext(Context.UPDATE, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				InputStream in = arg.getRequestBody();
				String input = readInputStream(in);
				ServerData server = getPreferredServer();

				if (server != null) {
					if (server.equals(Server.this.data)) {
						User user = gson.fromJson(input, User.class);
						login(user);

						ClientData client = getClient(user);
						client.setLastUpdate();
						Logger.log(client.getUser().getName() + " updates");
					} else {
						Logger.log("Some Server wants to update => Server gets transferred");
						Server.super.send(Context.UPDATE, server.getIp(), input, 3000, 5000);
					}
				}
				
				ServerResponse serverResponse = new ServerResponse(Server.this.data);
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
				String response = "";
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});

		server.createContext(Context.SERVER_IDENTIFY, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				String input = readInputStream(arg.getRequestBody());;
				Logger.log(input + " tries to identify");

				String response = gson.toJson(Server.this.ID, long.class);
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
		
		server.createContext(Context.SERVER_TASKS, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				ServerTasks tasks = gson.fromJson(readInputStream(arg.getRequestBody()), ServerTasks.class);
				Logger.log("Received Tasks");
				
				tasks.invoke(Server.this);

				String response = "";
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});

		server.createContext(Context.SYNCHRONIZE, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				FileSaver[] files = gson.fromJson(readInputStream(arg.getRequestBody()), FileSaver[].class);
				Logger.log("Receiving synchronizing data - " + files.length + " updated");
				
				if(!isPrimary()) saveSynchronizeFiles(files);
				
				String response = "";
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
		
		server.createContext(Context.REQUEST_SYNCHRONIZATION, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				arg.getRequestBody();
				FileSaver[] files = getSynchronizeFiles();
				String response = gson.toJson(files, files.getClass());
				arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream output = arg.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		});
	}
	
	private void login(User user) {
		if (getClient(user) == null) {
			clients.add(new ClientData(user));
			Logger.log(user.getName() + " logged in", Level.IMPORTANT_INFO);
		}
	}
	
	/**
	 * Returns server with highest priority which is online
	 */
	private ServerData getPreferredServer() {
		ServerData server = null;
		for (ServerData s : Constants.settings.getServers()) {
			if (s.isOnline()) {
				if (server == null || s.getPriority() < server.getPriority()) {
					server = s;
				}
			}
		}
		if (server == null)
			System.out.println("All Servers all down ");
		return server;
	}
	
	private ServerData[] getSortedServers() {
		ServerData[] servers = Constants.settings.getServers().clone();
		Arrays.sort(servers);
		return servers;
	}

	private ClientData getClient(User user) {
		for (ClientData client : clients) {
			if (client.getUser().equals(user)) {
				return client;
			}
		}
		return null;
	}

	/**
	 * Server tries to find its ip
	 */
	private void identify() {
		for (ServerData server : Constants.settings.getServers()) {
			try {
				String gsonResponse = super.send(Context.SERVER_IDENTIFY, server.getIp(), Tools.getIp(), 3000, 5000);
				long response = gson.fromJson(gsonResponse, long.class);
				if (response == this.ID) {
					this.data = server;
					break;
				}
			} catch (SocketTimeoutException e) {
				Logger.log("Server " + server + " is down", Level.WARNING);
			} catch (Exception e) {
				Logger.log(e);
			}
		}

		if (this.data == null) {
			Logger.log("Couldnt identify", Level.WARNING);
		} else {
			Logger.log("Identified as " + data);
		}
	}
	
	private boolean isPrimary() {
		return getPreferredServer().equals(this.data);
	}
	
	public void start() {
		server.start();
		
		this.console = new Console(this);
		this.console.start();
		
		Logger.log("Server started", Level.IMPORTANT_INFO);
		
		while(!Tools.hasInternet());
		checkServerReachability();
		identify();
		requestSynchronization();

		// lambda is amazing #Best Java-8 Feature
		loop(() -> checkServerReachability(), SERVERS_REACHABILITY_CHECK_DELAY, true, false);
		loop(() -> synchronize(), SYNCHRONIZATION_DELAY, true, true);
		loop(() -> checkClientConnection(), CLIENT_LOGOUT_TIME * 2, true, false);
	}

	public void stop() {
		console.exit();
		server.stop(0);
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.start();
	}

	private void loop(Runnable runnable, int delay, boolean thread, boolean wait) {
		Timer loop = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Thread t = new Thread(runnable);
				if (thread) {
					t.start();
				} else {
					t.run();
				}
			}
		});
		if (!wait) {
			loop.setInitialDelay(0);
		}
		loop.start();
	}

	/**
	 * Checks whether client disconnected
	 */
	private void checkClientConnection() {
		if(clients.isEmpty()) return;
		
		List<ClientData> toRemove = new ArrayList<ClientData>();
		for (ClientData client : clients) {
			if (System.currentTimeMillis() - client.getLastUpdate() >= CLIENT_LOGOUT_TIME) {
				toRemove.add(client);
			}
		}
		clients.removeAll(toRemove);

		for (ClientData client : toRemove) {
			Logger.log(client.getUser().getName() + " logged out", Level.IMPORTANT_INFO);
		}
	}

	/**
	 * Checks which server is online
	 */
	private void checkServerReachability() {
		if (!hasCheckedReachability)
			return;
		
		try {
			if (!Tools.hasInternet()) {
				Logger.log("No internet connection", Level.WARNING);
				return;
			}
			
			hasCheckedReachability = false;
			
			for (ServerData server : Constants.settings.getServers()) {
				Logger.log("Checking status of Server " + server);
				
				try {
					super.send(Context.REACHABILITY_CHECK, server.getIp(), "", 3000, 5000);
					server.setOnline(true);
					Logger.log("Server " + server + " is online");
				} catch (SocketTimeoutException e) { // is not reachable
					server.setOnline(false);
					Logger.log("Server " + server + " is down", Level.WARNING);
				} catch (UnknownHostException e) { // host does not exist
					server.setOnline(false);
					Logger.log("Unknown Adress: " + server.getIp());
				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}

		hasCheckedReachability = true;
	}
	
	private void saveSynchronizeFiles(FileSaver[] files) throws IOException {
		Tools.deleteDirectory(new File(Constants.SYNCHRONIZE));
		new File(Constants.SYNCHRONIZE).mkdir();
		for (FileSaver file : files) {
			FileOutputStream out = new FileOutputStream(file.getFile());
			out.write(file.getContent());
			out.close();
			// Files.write(file.getFile().toPath(), file.getContent());
		}
	}
	
	/**
	 * returns synchronizable files
	 */
	private FileSaver[] getSynchronizeFiles() {
		File[] files = Tools.listFiles(Constants.SYNCHRONIZE);
		FileSaver[] fileSavers = new FileSaver[files.length];
		for (int i = 0; i < files.length; i++) {
			try {
				fileSavers[i] = new FileSaver(files[i]);
			} catch (IOException e) {
				Logger.log(e);
			}
		}
		return fileSavers;
	}
	
	private void requestSynchronization() {
		Logger.log("Requesting synchronizable files...", Level.IMPORTANT_INFO);
		
		ServerData[] servers = getSortedServers();
		if(servers.length >= 2) {
			ServerData server = isPrimary() ? servers[1] : servers[0];
			if(server.isOnline()) {
				try {
					String gsonResponse = super.send(Context.REQUEST_SYNCHRONIZATION, server.getIp(), "", 5000, 5000);
					FileSaver[] files = gson.fromJson(gsonResponse, FileSaver[].class);
					saveSynchronizeFiles(files);
				} catch (IOException e) {
					Logger.log(e);
				}
			} else {
				Logger.log("Requesting synchronizable files failed - former primary server is not online", Level.WARNING);
			}
		} else {
			Logger.log("Requesting synchronizable files failed - only one server is registered", Level.WARNING);
		}
		
		Logger.log("Requesting synchronizable files finished...", Level.IMPORTANT_INFO);
	}
	
	private void synchronize() {
		ServerData preferredServer = getPreferredServer();
		if (preferredServer == null || !preferredServer.equals(data)) return;
		
		Logger.log("Synchronizing data...");
		
		//stopwatch
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		FileSaver[] files = getSynchronizeFiles();
		boolean changed = !Arrays.equals(files, this.synchronizedFiles);
		this.synchronizedFiles = files;
		if(!changed) {
			Logger.log("Synchronizing canceled - No local changes");
			return;
		}
		
		//send files to each server
		for (ServerData server : Constants.settings.getServers()) {
			if (server.equals(data) || !server.isOnline())
				continue;
			try {
				super.send(Context.SYNCHRONIZE, server.getIp(), files, 0, 0);
			} catch (IOException e) {
				Logger.log(e);
			}
		}
		
		stopwatch.stop();
		Logger.log("Synchronizing finished - it took " + Tools.round(stopwatch.getDurationInSeconds(), 2) + "s");
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
		return Encoder.decode(response.toString());
	}
	
	public Console getConsole() {
		return this.console;
	}

}
