package tools;

public enum Level {
	INFO, WARNING, ERROR;

	private boolean display = true;

	private Level() {
		
	}

	public boolean display() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}
}