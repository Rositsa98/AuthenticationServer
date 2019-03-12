package bg.sofia.uni.fmi.mjt.authentication.connection;

public enum ConnectionMessages { 
	SUCCESSFUL_REGISTRATION("Successful registration."),
	FAIL_REGISTRATION_EXISTING_USER("There is already a user with such username. Please choose another one."),
	LOG_OUT_TO_REGISTER_NEW_USER("Please logout first to register new user."),
	USER_NOT_FOUND_REGISTER_FIRST("No such user to be logged. Register first."),
	LOGGED_IN_NEWER_SESSION("Logged in newer session"),
	LOGGED_IN_NEW_SESSION("Logged in new session"),
	LOGGED_IN_BY_SESSION("Logged in with session id in existing session with tll: "),
	PASS_RESEST_SUCCESS("Password reset successfull."),
	PASS_RESET_FAIL("Password reset unsuccessfull."),
	UPDATE_SUCCESS("User updade successfull."),
	UPDATE_FAIL("User updade not successfull."),
	LOG_IN_TO_UPDATE("Log in first to update user."),
	LOG_OUT_SUCCESS("User logged out."),
	LOG_OUT_FAIL("User not logged out."),
	LOG_IN_TO_LOG_OUT("Log in first to be able to log out."),
	DELETE_SUCCESS("Account deleted."),
	LOG_IN_TO_DELETE("Log in first to be able to delete your account."),
	SESSION_NOT_ACTIVE("Session not active. Log in with your username and password first.");

	private String messageText;

	ConnectionMessages(String message) {
		messageText = message;
	}

	public String getMessage() {
		return messageText;
	}
};
