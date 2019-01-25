package server;

public class ServerData implements Comparable<ServerData> {
	
	private final String IP;
	private final int PRIORITY;
	private final String NAME;
	
	private boolean online;
	
	public ServerData(String ip, int priority, String name) {
		this.IP = ip;
		this.PRIORITY = priority;
		this.NAME = name;
		this.online = false;
	}
	
	public int compareTo(ServerData other){
		if(this.PRIORITY > other.PRIORITY) {
			if(this.online == other.online) {
				return 1;
			} else {
				return other.online ? 1 : -1;
			}
		} else if(this.PRIORITY < other.PRIORITY) {
			if(this.online == other.online) {
				return -1;
			} else {
				return other.online ? 1 : -1;
			}
		} return 0;
    }
	
	@Override
	public String toString() {
		return "\"" + NAME + "\"";
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
	public String getName() {
		return NAME;
	}
	
}
