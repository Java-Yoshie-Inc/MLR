package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class User {

	private String IP;
	private String LOCAL_IP;
	private String NAME;

	public User(String ip, String localIp, String name) {
		this.IP = ip;
		this.LOCAL_IP = localIp;
		this.NAME = name;
	}

	public User() {
		try {
			this.IP = getMachineIP();
			this.LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
			this.NAME = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return NAME+","+IP+","+LOCAL_IP;
	}

	private String getMachineIP() throws IOException {
		URL url_name = new URL("http://bot.whatismyipaddress.com");
        BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream())); 
        return sc.readLine().trim();
	}

	public String getIp() {
		return IP;
	}

	public String getLocalIp() {
		return LOCAL_IP;
	}

	public String getName() {
		return NAME;
	}

}
