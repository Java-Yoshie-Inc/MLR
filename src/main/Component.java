package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tools.Constants;

public abstract class Component {
	
	protected static final Random random = new Random();
	protected static final Gson gson = new GsonBuilder().create();
	
	public Component() {
		
	}
	
	protected final String send(String context, String ip, String gsonString, int connectTimeout, int readTimeout) throws IOException {
		URL obj = new URL("http://" + ip + ":" + Constants.settings.getPort() + context);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setConnectTimeout(connectTimeout);
		con.setReadTimeout(readTimeout);
		
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		
		OutputStream output = con.getOutputStream();
		output.write(gsonString.getBytes());
		output.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		StringBuffer gsonResponse = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			gsonResponse.append(inputLine);
		}
		in.close();
		
		return gsonResponse.toString();
	}
	
	protected final String send(String context, String ip, Object object, int connectTimeout, int readTimeout) throws IOException {
		return send(context, ip, gson.toJson(object, object.getClass()), connectTimeout, readTimeout);
	}
	
}
