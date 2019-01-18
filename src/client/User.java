package client;

import tools.Tools;

public class User {

	private String IP;
	private String LOCAL_IP;
	private String NAME;

	public User(String ip, String localIp, String name) {
		this.IP = ip;
		this.LOCAL_IP = localIp;
		this.NAME = name;
	}

	public User() {
		try {
			this.IP = Tools.getIp();
			this.LOCAL_IP = Tools.getLocalIp();
			this.NAME = Tools.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return NAME+","+IP+","+LOCAL_IP;
	}
	
	public String getIp() {
		return IP;
	}

	public String getLocalIp() {
		return LOCAL_IP;
	}

	public String getName() {
		return NAME;
	}

}
