package server;

import java.lang.reflect.InvocationTargetException;

import tools.Tools;

public class ServerTasks {
	
	private StringBuilder tasks = new StringBuilder();
	
	public ServerTasks() {
		
	}
	
	public void invoke(Server server) {
		for(String method : tasks.toString().split(";")) {
			try {
				this.getClass().getDeclaredMethod(method.toUpperCase(), Server.class).invoke(this, server);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void append() {
		tasks.append(Tools.getMethodName(1) + ";");
	}
	
	@SuppressWarnings("unused")
	private void STOP(Server server) {
		server.stop();
	}
	
	public void stop() {
		append();
	}

}
