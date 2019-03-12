package bg.sofia.uni.fmi.mjt.authentication.connection;

import java.util.concurrent.Callable;

public class SessionTask implements Callable<String> {

	private int timeToLive;
	private boolean isAlive;

	SessionTask() {
		isAlive = true;
	}

	@Override
	public String call() throws Exception {

		Thread.sleep(timeToLive);
		isAlive = false;

		return "Session ready!";
	}
	
	boolean getIsAlive() {
		return isAlive;
	}

	void setTaskTimeToLive(int time) {
		this.timeToLive = time;
	}

}
