package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Component {
	
	protected static final Gson gson = new GsonBuilder().create();
	
	public Component() {
		
	}
	
	protected final String send(String context, String ip, String gsonString) throws IOException {
		URL obj = new URL("http://" + ip + context);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setConnectTimeout(3*1000);
		con.setReadTimeout(5*1000);
		
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
	
	protected final String send(String context, String ip, Object object) throws IOException {
		return send(context, ip, gson.toJson(object, object.getClass()));
	}
	
}
