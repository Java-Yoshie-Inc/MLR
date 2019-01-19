package tools;

import java.io.IOException;

public class Constants {
	
	public static ServerData[] SERVERS;
	public static final String UPDATE_CONTEXT = "/update";
	public static final String REACHABILITY_CHECK_CONTEXT = "/checkstatus";
	public static final String NAME = "SOSE";
	public static final String FULL_NAME = "Self-Organizing-Server-Empire";
	public static final String LOGIN_CONTEXT = "/login";
	
	static {
		try {
			SERVERS = Tools.readSettings().getServers();
		} catch (IOException e) {
			SERVERS = new ServerData[0];
			e.printStackTrace();
		}
	}
	
}
