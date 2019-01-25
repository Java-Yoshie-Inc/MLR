package main;

import logger.Level;
import logger.Logger;
import tools.Settings;
import tools.Tools;

public class Constants {
	
	public static Settings settings;
	public static final String NAME = "SOSE";
	public static final String FULL_NAME = "Self-Organizing-Server-Empire";
	public static final String DATA_PATH = "data/";
	public static final String SYNCHRONIZE = DATA_PATH + "synchronize/";
	
	static {
		try {
			settings = Tools.readSettings();
		} catch (Exception e) {
			Logger.log(e.getMessage(), Level.ERROR);
		}
	}
	
}
