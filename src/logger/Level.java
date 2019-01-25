package logger;

import java.awt.Color;

public enum Level {
	
	INFO(new Color(30, 30, 30)), 
	IMPORTANT_INFO(Color.BLACK), 
	WARNING(new Color(255, 120, 0)), 
	ERROR(Color.RED);

	private boolean display = true;
	private final Color color;

	private Level(Color color) {
		this.color = color;
	}

	public boolean display() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public Color getColor() {
		return color;
	}
}