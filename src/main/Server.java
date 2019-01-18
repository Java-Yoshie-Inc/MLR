package main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	private String[] SERVER_IPS = new String[] {
			"localhost", 
	};
	
	private Gson gson = new GsonBuilder().create();
	
	private int PORT = 2026;
	private HttpServer server;
	private final int HTTP_OK_STATUS = 200;
	
	private final String NAME = "IntexServer";
	
	public Server() throws Exception {
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		HttpHandler update = new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				InputStream in = arg.getRequestBody();
				User user = gson.fromJson(readInputStream(in), User.class);
				
				System.out.println(user);
				
				ServerResponse serverResponse = new ServerResponse(NAME);
				String gsonString = gson.toJson(serverResponse, ServerResponse.class);
				arg.sendResponseHeaders(HTTP_OK_STATUS, gsonString.length());
				OutputStream output = arg.getResponseBody();
				output.write(gsonString.getBytes());
				output.close();
			}
		};
		
		server.createContext("/update", update);
		
		server.setExecutor(null);
		server.start();
	}
	
	public static void main(String[] args) throws Exception {
		new Server();
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
