package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

	public enum Level {
		INFO, WARNING, ERROR
	}

	private static StringBuilder sb = new StringBuilder();

	public static String getLog() {
		return sb.toString();
	}

	public static void log(String text, Level level) {
		StringBuilder sb = new StringBuilder();
		sb.append(getTime());
		sb.append(" ");
		if (level != Level.INFO) {
			sb.append(level + ": ");
		}
		sb.append(text + System.lineSeparator());
		Logger.sb.append(sb);
		
		try {
			Files.write(Paths.get(Constants.DATA_PATH + "log.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(Exception e) {
		log(e.getClass() + " : " + e.getMessage(), Level.ERROR);
	}

	public static void log(String text) {
		log(text, Level.INFO);
	}
	
	public static void log() {
		log("");
	}
	
	private static String getTime() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("[dd.MM.yy-HH:mm:ss]");
        return sdf.format(cal.getTime());
	}

}
