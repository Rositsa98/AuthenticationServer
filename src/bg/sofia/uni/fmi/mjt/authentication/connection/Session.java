package bg.sofia.uni.fmi.mjt.authentication.connection;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Session implements Runnable {

	private int timeToLive;
	private int id;

	private ExecutorService executor;
	private Future<String> future;

	private final static int THREAD_POOL_SIZE = 50;

	public Session() {
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	public Session(String username, int timeToLive) {
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.timeToLive = timeToLive;
		this.id = username.hashCode();
	}

	@Override
	public void run() {
		SessionTask sampleTask = new SessionTask();
		sampleTask.setTaskTimeToLive(timeToLive);

		future = executor.submit(sampleTask);

		try { // starting session for time to live and messages printed to see progress
			System.out.println("Session started..");
			try {
				System.out.println(future.get(timeToLive, TimeUnit.SECONDS));
				if (timeToLive != 0 && future.isCancelled() == false) {
					System.out.println(future.get(timeToLive, TimeUnit.SECONDS));
				} else {
					future.cancel(true);
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Thread exceptions when session is being started triggered.");
				}
			System.out.println("Session finished!");
		} catch (TimeoutException | CancellationException e) {
			future.cancel(true);
			System.out.println("Session terminated!");
		}

		if (sampleTask.getIsAlive() == false) {
			timeToLive = 0;
		}

		executor.shutdownNow();
	}

	public int getId() {
		return id;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void terminateSession() {
		if (!future.isCancelled()) {
			future.cancel(true);
			timeToLive = 0;
		}
	}

	public void setTimeToLive(int time) {
		timeToLive = time;
	}
}
