package tools;

import server.ServerData;

public class Settings {
	
	private final ServerData[] servers;
	private final int PORT;
	
	public Settings(ServerData[] servers, int port) {
		this.servers = servers;
		this.PORT = port;
	}

	public ServerData[] getServers() {
		return servers;
	}
	public int getPort() {
		return PORT;
	}
	
}
