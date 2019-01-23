package tools;

public class Stopwatch {
	
	private long startTime = 0;
	private long endTime = 0;
	private boolean wasStarted = false;
	
	public Stopwatch() {
		
	}
	
	public void start() {
		wasStarted = true;
		startTime = System.nanoTime();
		endTime = 0;
	}
	
	public void stop() {
		if(wasStarted) {
			endTime = System.nanoTime();
			wasStarted = false;
		}
	}
	
	private long calculateDuration() {
		return ((endTime == 0) ? System.nanoTime() : endTime) - startTime;
	}
	
	public long getDurationInMillis() {
		return Math.round(calculateDuration() / 1000000d);
	}
	public double getDurationInSeconds() {
		return calculateDuration()/1000000d/1000d;
	}
	public long getDurationInNanos() {
		return calculateDuration();
	}
	public boolean wasStarted() {
		return this.wasStarted;
	}

}
