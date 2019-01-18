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
import tools.Tools;

public class Server {
	
	private String IP = Tools.getIp();
	private String LOCAL_IP = Tools.getLocalIp();
	private int PORT = 2026;
	private final int HTTP_OK_STATUS = 200;
	private final String NAME = "IntexServer";

	private Gson gson = new GsonBuilder().create();
	private HttpServer server;
	private Console console;
	private Timer loop;
	
	private boolean usePublicIP = false;
	

	public Server() throws IOException {
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (BindException e) {
			System.out.println("Server is already running on this ip");
			return;
		}
		
		server.setExecutor(null);

		server.createContext(Constants.UPDATE_CONTEXT, new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				InputStream in = arg.getRequestBody();
				User user = gson.fromJson(readInputStream(in), User.class);

				System.out.println(user.getName() + " updates");

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
		loop();
		//console = new Console(this);
	}
	
	public void stop() {
		loop.stop();
		server.stop(0);
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.start();
	}

	private void loop() {
		loop = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					checkServerReachability();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		loop.setInitialDelay(0);
		loop.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void checkServerReachability() throws IOException {
		for(String server : Constants.SERVER_IPS) {
			if(usePublicIP && server.equals(IP) || server.equals(LOCAL_IP)) {
				continue;
			}
			
			System.out.println("Checking for status of " + server);
			
			try {
				String url = "http://" + server + ":" + PORT + Constants.REACHABILITY_CHECK_CONTEXT;
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				
				OutputStream output = con.getOutputStream();
				output.write("".getBytes());
				output.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				StringBuffer gsonResponse = new StringBuffer();
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					gsonResponse.append(inputLine);
				}
				in.close();

				con.getInputStream();
				
				System.out.println("Server " + server + " is online");
			} catch (ConnectException | FileNotFoundException e) {
				System.out.println(server + " is down");
			}
		}
		System.out.println();
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
