package bg.sofia.uni.fmi.mjt.authentication.server;

import bg.sofia.uni.fmi.mjt.authentication.client.User;
import bg.sofia.uni.fmi.mjt.authentication.connection.ConnectionRunnable;
import bg.sofia.uni.fmi.mjt.authentication.connection.PasswordCryptor;
import bg.sofia.uni.fmi.mjt.authentication.connection.Session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ServerActivities {

	private final static int PORT = 8080;
	private final static int SESSION_TIME_TO_LIVE = 1000000;
	private static int numberGuest = 0;

	private Map<String, User> registeredUsers = new ConcurrentHashMap<>();
	private Map<String, Session> activeSessions = new ConcurrentHashMap<>();
	private File usersData = new File("resources/usersData.txt");

	public Server() { // update maps data with file data
		try (BufferedReader usersDataReader = new BufferedReader(new FileReader("resources/usersData.txt"))) {
			String userInformation = "";
			while ((userInformation = usersDataReader.readLine()) != null) {

				String[] userInformationSplitted = userInformation.split(" ");
				String username = userInformationSplitted[ArrayIndexes.INDEX_0.getValue()];
				String password = userInformationSplitted[ArrayIndexes.INDEX_1.getValue()];
				String firstName = userInformationSplitted[ArrayIndexes.INDEX_2.getValue()];
				String lastName = userInformationSplitted[ArrayIndexes.INDEX_3.getValue()];
				String email = userInformationSplitted[ArrayIndexes.INDEX_4.getValue()];
				User user = new User(username, password, firstName, lastName, email);
				registeredUsers.put(username, user);
			}
		} catch (IOException e) {
			System.out.println("IOException when updating maps data with file data in Server constructor.");
		}
	}

	private void runServer() {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.printf(ServerMessages.SERVER_RUNNING.getMessage(), PORT);

			while (true) { // accept clients
				Socket clientSocket = serverSocket.accept();

				numberGuest += 1;
				String guestUsername = "guest" + numberGuest;
				System.out.println(guestUsername + " connected");

				// establish a connection and read and execute client's commands
				ConnectionRunnable connection = new ConnectionRunnable(guestUsername, clientSocket, this);
				new Thread(connection).start();
			}

		} catch (IOException e) {
			System.out.println(ServerMessages.SERVER_RUNNING_FAIL.getMessage() + PORT);
		}
	}

	public boolean isUsernameRegistered(String username) {
		return registeredUsers.containsKey(username);
	}

	public boolean isThereUserWithPassword(String username, String password) throws Exception {
		return registeredUsers.get(username).getPassword().equals(PasswordCryptor.encryptToHex(password));
	}

	public boolean isUserRegistered(String username, String password) throws Exception {
		return isUsernameRegistered(username) && isThereUserWithPassword(username, password);
	}

	@Override
	public boolean canRegisterUser(User client) throws Exception {

		synchronized (client.getUsername()) {
			if (!isUsernameRegistered(client.getUsername())) {
				registeredUsers.put(client.getUsername(), client);

				try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(usersData, true))) {
					dataWriter.append(client.getUsername() + " " + client.getPassword() + " " + client.getFirstName()
							+ " " + client.getLastName() + " " + client.getEmail() + " " + client.getClientSocket()
							+ System.lineSeparator());
				} catch (IOException e) {
					System.out.println("IOExceprion thrown when writing registration information in file.");
				}

				System.out.println(ServerMessages.REGISTER.getMessage() + client.getUsername());
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String logInWithUsername(String username, String password) {
		Session newSession = new Session(username, SESSION_TIME_TO_LIVE);

		synchronized (this) {
			new Thread(newSession).start();
			activeSessions.put(username, newSession);
		}
		System.out.println(ServerMessages.LOG_IN.getMessage() + username);
		return newSession.getId() + " " + newSession.getTimeToLive() + "";
	}

	@Override
	synchronized public int logInWithSession(int sessionId) {
		for (Session session : activeSessions.values()) {
			if (session.getId() == sessionId) {
				System.out.println(ServerMessages.LOG_IN_WITH_SESSION.getMessage() + sessionId);
				return session.getTimeToLive();
			}
		}
		return -1;
	}

	public Map<String, User> getUsers() {
		return registeredUsers;
	}

	public Map<String, Session> getSessions() {
		return activeSessions;
	}

	public boolean isSessionActive(String username) {
		cleanSessions();
		return activeSessions.containsKey(username);
	}

	public boolean isSessionActiveBySessionId(int sessionId) {
		cleanSessions();
		for (Session session : activeSessions.values()) {
			if (session.getId() == sessionId) {
				return true;
			}
		}
		return false;
	}

	public synchronized void removeSession(String username) {
		for (Entry<String, Session> session : activeSessions.entrySet()) {
			if (session.getKey().equals(username)) {
				if (session.getValue().getTimeToLive() != 0) {
					session.getValue().terminateSession();
				}
				activeSessions.remove(username);
				break;
			}
		}
	}

	public synchronized void cleanSessions() {
		for (Entry<String, Session> session : activeSessions.entrySet()) {
			if (session.getValue().getTimeToLive() == 0) {
				activeSessions.remove(session.getKey());
			}
		}
	}

	public String getUsernameBySessionId(int sessionId) {
		for (Entry<String, Session> session : activeSessions.entrySet()) {
			if (session.getValue().getId() == sessionId) {
				return session.getKey();
			}
		}
		return null;
	}

	private synchronized void editUsersFile() {

		try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(usersData))) {
			for (User user : registeredUsers.values()) {
				dataWriter.write(user.getUsername() + " " + user.getPassword() + " " + user.getFirstName() + " "
						+ user.getLastName() + " " + user.getEmail() + " " + System.lineSeparator());
			}
		} catch (IOException e) {
			System.out.println("IOExceprion thrown when writing registration information in file.");
			e.printStackTrace();
		}
	}

	@Override
	public void updateUser(String oldUsername, User user) {
		System.out.println(ServerMessages.UPDATE.getMessage() + oldUsername);
		registeredUsers.remove(oldUsername);

		registeredUsers.put(user.getUsername(), user);
		editUsersFile();
	}

	@Override
	public boolean canLogout(int sessionId) {
		String username = getUsernameBySessionId(sessionId);
		if (username != null) {
			System.out.println(ServerMessages.LOG_OUT.getMessage() + username);
			activeSessions.remove(username);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteUser(String username) {
		registeredUsers.remove(username);
		activeSessions.remove(username);
		System.out.println(ServerMessages.DELETE.getMessage() + username);

		editUsersFile();
	}

	public static void main(String[] args) {
		new Server().runServer();
	}

}
