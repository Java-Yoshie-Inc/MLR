package tools;

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
		sb.append(getTime());
		sb.append(" ");
		if (level != Level.INFO) {
			sb.append(level + ": ");
		}
		sb.append(text + System.lineSeparator());
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
