package client;

import java.lang.reflect.Field;

import tools.Tools;

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
			this.IP = Tools.getIp();
			this.LOCAL_IP = Tools.getLocalIp();
			this.NAME = Tools.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return NAME+","+IP+","+LOCAL_IP;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			Field[] fields1 = this.getClass().getDeclaredFields();
			Field[] fields2 = obj.getClass().getDeclaredFields();
			
			for(int i=0; i < fields1.length; i++) {
				Object field1 = fields1[i].get(this);
				Object field2 = fields2[i].get(obj);
				if(!field1.equals(field2)) {
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
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
