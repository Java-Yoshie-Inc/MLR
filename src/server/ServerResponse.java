package server;

public class ServerResponse {
	
	private final ServerData server;
	
	public ServerResponse(ServerData server) {
		this.server = server;
	}
	
	public ServerData getServer() {
		return this.server;
	}
	
}
