package tools;

public class ServerData {
	
	private final String IP;
	private final int PRIORITY;
	
	private boolean online;
	
	public ServerData(String ip, int priority) {
		this.IP = ip;
		this.PRIORITY = priority;
		this.online = true;
	}
	
	public String getIP() {
		return IP;
	}
	public int getPRIORITY() {
		return PRIORITY;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean b) {
		this.online = b;
	}
	
}
