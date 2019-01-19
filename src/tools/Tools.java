package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;

import com.google.gson.GsonBuilder;

public class Tools {
	
	public static String getIp() throws IOException {
		URL url_name = new URL("http://bot.whatismyipaddress.com");
        BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream())); 
        return sc.readLine().trim();
	}
	
	public static String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
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
	
}
