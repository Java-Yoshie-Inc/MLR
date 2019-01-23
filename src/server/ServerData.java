package server;

public class ServerData {
	
	private final String IP;
	private final int PRIORITY;
	
	private boolean online;
	
	public ServerData(String ip, int priority) {
		this.IP = ip;
		this.PRIORITY = priority;
		this.online = false;
	}
	
	@Override
	public String toString() {
		return IP;
	}
	
	public String getIp() {
		return IP;
	}
	public int getPriority() {
		return PRIORITY;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean b) {
		this.online = b;
	}
	
}
