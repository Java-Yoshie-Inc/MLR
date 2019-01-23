package client;

public class ClientData {
	
	private final User user;
	private long lastUpdate = System.currentTimeMillis();
	
	public ClientData(User user) {
		this.user = user;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate() {
		this.lastUpdate = System.currentTimeMillis();
	}

	public User getUser() {
		return user;
	}
	
}
