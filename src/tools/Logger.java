package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	
	private static final File file = new File(Constants.DATA_PATH + "log.log");
	
	static {
		long length = file.length();
		if(length > 100000) {
			try {
				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		
		if(level.display()) {
			Logger.sb.append(sb);
		}
		
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(file, true)); 
			writer.append(sb.toString());
			writer.close();
		} catch (Exception e) {
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
