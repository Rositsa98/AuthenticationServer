package bg.sofia.uni.fmi.mjt.authentication.connection;

public interface ConnectionCommands {
	void register(String username, String password, String firstName, String lastName, String email) throws Exception;

	void logIn(String[] splittedCommand) throws Exception;

	String[] logInWithUsername(String username, String password) throws Exception;

	int logInWithSession(int sessionId);

	void resetPassword(String username, String oldPassword, String newPassword) throws Exception;

	void updateUser(String[] splittedCommand);

	void logout(int sessionId);

	void deleteUser(String username);
}
