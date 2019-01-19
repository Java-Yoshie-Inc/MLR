package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
		String content = new String(Files.readAllBytes(new File("data/settings.txt").toPath()));
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
	
}
