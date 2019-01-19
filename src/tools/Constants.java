package tools;

import java.io.IOException;

public class Constants {
	
	public static ServerData[] SERVERS;
	public static final String[] SERVER_IPS = new String[] { "192.168.178.21", "192.168.178.31" };
	public static final String UPDATE_CONTEXT = "/update";
	public static final String REACHABILITY_CHECK_CONTEXT = "/checkstatus";
	
	static {
		try {
			SERVERS = Tools.readSettings().getServers();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
