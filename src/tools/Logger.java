package tools;

public class Logger {

	public enum Level {
		INFO, WARNING, ERROR
	}

	private static StringBuilder sb = new StringBuilder();

	public static String getLog() {
		return sb.toString();
	}

	public static void log(String text, Level level) {
		if (level != Level.INFO) {
			sb.append(level + ": ");
		}
		sb.append(text + System.lineSeparator());
	}

	public static void log(String text) {
		log(text, Level.INFO);
	}
	
	public static void log() {
		log("");
	}

}
