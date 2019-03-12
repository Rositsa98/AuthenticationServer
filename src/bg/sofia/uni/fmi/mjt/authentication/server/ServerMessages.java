package bg.sofia.uni.fmi.mjt.authentication.server;

public enum ServerMessages {
	SERVER_RUNNING("Server is running on localhost:%d%n"),
	SERVER_RUNNING_FAIL("Maybe another server is running on port "), REGISTER("Registered user "),
	LOG_IN("Loged in user "), LOG_IN_WITH_SESSION("Loged in user with session "), UPDATE("Updating "),
	LOG_OUT("Log out user "), DELETE("Deleted user ");

	private String message;

	ServerMessages(String messageText) {
		this.message = messageText;
	}

	public String getMessage() {
		return message;
	}

}
