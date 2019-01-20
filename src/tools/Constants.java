package tools;

import tools.Logger.Level;

public class Constants {
	
	public static ServerData[] SERVERS;
	public static final String UPDATE_CONTEXT = "/update";
	public static final String REACHABILITY_CHECK_CONTEXT = "/checkstatus";
	public static final String NAME = "SOSE";
	public static final String FULL_NAME = "Self-Organizing-Server-Empire";
	
	static {
		try {
			SERVERS = Tools.readSettings().getServers();
		} catch (Exception e) {
			SERVERS = new ServerData[0];
			Logger.log(e.getMessage(), Level.ERROR);
		}
	}
	
}
