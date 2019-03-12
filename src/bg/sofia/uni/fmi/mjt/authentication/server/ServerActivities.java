package bg.sofia.uni.fmi.mjt.authentication.server;

import bg.sofia.uni.fmi.mjt.authentication.client.User;

public interface ServerActivities {
	boolean canRegisterUser(User user) throws Exception;

	String logInWithUsername(String username, String password);

	int logInWithSession(int sessiontId);

	void updateUser(String oldUsername, User user);

	boolean canLogout(int sessionId);

	void deleteUser(String username);

}
