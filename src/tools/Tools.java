package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.GsonBuilder;

import main.Constants;

public class Tools {

	public static String getIp() throws IOException {
		URL url = new URL("http://bot.whatismyipaddress.com");
		BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));
		return sc.readLine().trim();
	}

	public static String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static String getName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static Settings readSettings() throws IOException {
		String content = new String(Files.readAllBytes(new File(Constants.DATA_PATH + "settings.txt").toPath()));
		return new GsonBuilder().create().fromJson(content, Settings.class);
	}

	public static boolean hasInternet() {
		try {
			final URL url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static File[] listFiles(String directoryName) {
		File directory = new File(directoryName);
		List<File> files = new ArrayList<File>();

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				files.addAll(Arrays.asList(listFiles(file.getAbsolutePath())));
			}
		}
		return files.toArray(new File[0]);
	}

	public static String toString(double d) {
		return new BigDecimal(d).toPlainString();
	}

	public static double round(double d, int decimalPlaces) {
		DecimalFormat df = new DecimalFormat("#." + repeat("#", decimalPlaces));
		df.setRoundingMode(RoundingMode.CEILING);
		return Double.valueOf(df.format(d).replace(',', '.'));
	}

	public static void deleteDirectory(File dir) {
		if(dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDirectory(new File(dir, children[i]));
			}
		}
		dir.delete();
	}

	public static String repeat(String s, int count) {
		return new String(new char[count]).replace("\0", s);
	}

	public static boolean equals(Object object1, Object object2) {
		try {
			Field[] fields1 = object1.getClass().getDeclaredFields();
			Field[] fields2 = object2.getClass().getDeclaredFields();
			for (int i = 0; i < fields1.length; i++) {
				fields1[i].setAccessible(true);
				fields2[i].setAccessible(true);
				Object field1 = fields1[i].get(object1);
				Object field2 = fields2[i].get(object2);
				if (!field1.equals(field2)) {
					System.out.println(fields1[i].getName());
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
