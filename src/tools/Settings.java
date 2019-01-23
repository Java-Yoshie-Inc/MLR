package tools;

import server.ServerData;

public class Settings {
	
	private ServerData[] servers;
	
	public Settings(ServerData[] servers) {
		this.servers = servers;
	}

	public ServerData[] getServers() {
		return servers;
	}
	
}
