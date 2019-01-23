package tools;

import server.ServerData;
import tools.Logger.Level;

public class Constants {
	
	public static ServerData[] SERVERS;
	public static final String NAME = "SOSE";
	public static final String FULL_NAME = "Self-Organizing-Server-Empire";
	public static final String DATA_PATH = "data/";
	
	static {
		try {
			SERVERS = Tools.readSettings().getServers();
		} catch (Exception e) {
			SERVERS = new ServerData[0];
			Logger.log(e.getMessage(), Level.ERROR);
		}
	}
	
}
