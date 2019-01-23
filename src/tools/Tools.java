package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;

import com.google.gson.GsonBuilder;

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
	
	public static boolean equals(Object object1, Object object2) {
		try {
			Field[] fields1 = object1.getClass().getDeclaredFields();
			Field[] fields2 = object2.getClass().getDeclaredFields();
			for(int i=0; i < fields1.length; i++) {
				fields1[i].setAccessible(true);
				fields2[i].setAccessible(true);
				Object field1 = fields1[i].get(object1);
				Object field2 = fields2[i].get(object2);
				if(!field1.equals(field2)) {
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
