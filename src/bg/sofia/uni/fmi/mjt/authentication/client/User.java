package bg.sofia.uni.fmi.mjt.authentication.client;

import java.net.Socket;

public class User {

	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;

	private UserStatus status;
	private Socket clientSocket;

	private int sessionId;
	private int sessionTimeToLive;

	public User(String username) {
		this.username = username;
		this.status = UserStatus.GUEST;
	}

	public User(String username, Socket socket) {
		this.username = username;
		this.status = UserStatus.GUEST;
		this.clientSocket = socket;
	}

	public User(String username, String password, String firstName, String lastName, String email) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;

		this.status = UserStatus.REGISTERED;
	}

	public User(String username, String password, String firstName, String lastName, String email,
			Socket clientSocket) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.clientSocket = clientSocket;

		this.status = UserStatus.REGISTERED;
	}

	public void setUserLogged(int sessionId, int timeToLive) {
		this.status = UserStatus.LOGGED;
		this.sessionId = sessionId;
		this.sessionTimeToLive = timeToLive;
	}

	public String getUsername() {
		return username;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public String getPassword() {
		return this.password;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirstname(String firstName) {
		this.firstName = firstName;
	}

	public void setLastname(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

}
