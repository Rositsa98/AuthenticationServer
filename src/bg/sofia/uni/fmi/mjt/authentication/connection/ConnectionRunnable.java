package bg.sofia.uni.fmi.mjt.authentication.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import bg.sofia.uni.fmi.mjt.authentication.client.ClientCommands;
import bg.sofia.uni.fmi.mjt.authentication.client.User;
import bg.sofia.uni.fmi.mjt.authentication.client.UserStatus;
import bg.sofia.uni.fmi.mjt.authentication.server.ArrayIndexes;
import bg.sofia.uni.fmi.mjt.authentication.server.Server;

import java.util.Scanner;

public class ConnectionRunnable implements Runnable, ConnectionCommands {

	private User currentUser;
	private Server server;

	private BufferedReader commandReader;
	private PrintWriter messageSender;

	public ConnectionRunnable(String username, Socket clientSocket, Server server) {
		this.currentUser = new User(username, clientSocket);
		this.server = server;

		try {
			this.commandReader = new BufferedReader(
					new InputStreamReader(currentUser.getClientSocket().getInputStream()));

			this.messageSender = new PrintWriter(currentUser.getClientSocket().getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("IOException thrown when creating client commandReader and messageSender.");
		}
	}

	String getCurrentUserUsername() {
		return currentUser.getUsername();
	}

	@Override
	public void run() {

		try {
			while (true) {
				String commandLine = commandReader.readLine();

				if (commandLine != null) {
					String[] splittedCommand = commandLine.split(" ");
					String commandText = splittedCommand[ArrayIndexes.INDEX_0.getValue()];
					ClientCommands command = ClientCommands.findCommandByValue(commandText);

					switch (command) {
					case REGISTER: {
						String username = splittedCommand[ArrayIndexes.INDEX_1.getValue()];
						String password = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
						String firstName = splittedCommand[ArrayIndexes.INDEX_3.getValue()];
						String lastName = splittedCommand[ArrayIndexes.INDEX_4.getValue()];
						String email = splittedCommand[ArrayIndexes.INDEX_5.getValue()];
						register(username, password, firstName, lastName, email);
						break;
					}

					case LOGIN: {
						logIn(splittedCommand);
						// new Thread(new Session()).start();
						break;
					}

					case RESET_PASSWORD: {
						String username = splittedCommand[ArrayIndexes.INDEX_1.getValue()];
						String oldPass = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
						String newPass = splittedCommand[ArrayIndexes.INDEX_3.getValue()];

						resetPassword(username, oldPass, newPass);
						break;
					}

					case UPDATE_USER: {
						updateUser(splittedCommand);
						break;
					}

					case LOGOUT: {
						int sessionId = Integer.parseInt(splittedCommand[ArrayIndexes.INDEX_1.getValue()]);
						logout(sessionId);
						break;
					}

					case DELETE_USER: {
						String username = splittedCommand[ArrayIndexes.INDEX_1.getValue()];
						deleteUser(username);
						break;
					}

					default: {
						System.out.println("Command not found.");
						break;
					}
					}
				}

				else {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("IOException when reading commands found!");
			e.printStackTrace();
		}
	}

	@Override
	public void register(String username, String password, String firstName, String lastName, String email)
			throws Exception {

		if (currentUser.getStatus() != UserStatus.LOGGED) {

			try {
				currentUser = new User(username, PasswordCryptor.encryptToHex(password), firstName, lastName, email,
						currentUser.getClientSocket());

			} catch (Exception e) {
				System.out.println("Expetion when crypting password triggered.");
				e.printStackTrace();
			}

			if (server.canRegisterUser(currentUser)) {
				messageSender.println(ConnectionMessages.SUCCESSFUL_REGISTRATION.getMessage());
			} else {
				messageSender.println(ConnectionMessages.FAIL_REGISTRATION_EXISTING_USER.getMessage());
			}
		} else {
			messageSender.println(ConnectionMessages.LOG_OUT_TO_REGISTER_NEW_USER.getMessage());
		}
	}

	@Override
	public void logIn(String[] splittedCommand) throws Exception {
		int sessionId = 0;
		int ttl = 0;
		boolean isSuccesfullyLogged = false;

		if (splittedCommand.length > 2) {
			String username = splittedCommand[ArrayIndexes.INDEX_1.getValue()];
			String password = splittedCommand[ArrayIndexes.INDEX_2.getValue()];

			String[] response = logInWithUsername(username, password);

			if (response != null) {
				sessionId = Integer.parseInt(response[0]);
				ttl = Integer.parseInt(response[1]);
				isSuccesfullyLogged = true;
			}

		} else if (splittedCommand.length == 1
				&& server.isSessionActiveBySessionId(Integer.parseInt(splittedCommand[1]))) {
			ttl = logInWithSession(sessionId);

		} else {
			messageSender.println(ConnectionMessages.SESSION_NOT_ACTIVE.getMessage());
		}

		if (ttl != -1) {
			isSuccesfullyLogged = true;
		}

		if (isSuccesfullyLogged == true) {
			currentUser.setUserLogged(sessionId, ttl);
		}
	}

	@Override
	public String[] logInWithUsername(String username, String password) throws Exception {

		if (server.isUserRegistered(username, password)) {

			String[] response = null;
			if (server.isSessionActive(username)) { // if already a session
				server.removeSession(username);

				response = server.logInWithUsername(username, password).split(" ");
				messageSender.println(ConnectionMessages.LOGGED_IN_NEWER_SESSION.getMessage() + " with id: "
						+ response[ArrayIndexes.INDEX_0.getValue()] + " and ttl: "
						+ response[ArrayIndexes.INDEX_1.getValue()]);

			} else { // if there is no existing session

				response = server.logInWithUsername(username, password).split(" ");

				messageSender.println(ConnectionMessages.LOGGED_IN_NEW_SESSION.getMessage() + " with id: "
						+ response[ArrayIndexes.INDEX_0.getValue()] + " and ttl: "
						+ response[ArrayIndexes.INDEX_1.getValue()]);
			}
			currentUser = server.getUsers().get(username); // get all needed info so as to be really logged

			return response;
		}

		else {
			messageSender.println(ConnectionMessages.USER_NOT_FOUND_REGISTER_FIRST.getMessage());
			return null;
		}
	}

	@Override
	public int logInWithSession(int sessionId) {
		int ttl = server.logInWithSession(sessionId);

		if (ttl != -1) {

			messageSender.println(ConnectionMessages.LOGGED_IN_BY_SESSION.getMessage() + ttl);

			String username = server.getUsernameBySessionId(sessionId); // check null
			currentUser = server.getUsers().get(username); // get all needed info so as to be really logged

			return ttl;
		} else {
			return -1;
		}
	}

	@Override
	public void resetPassword(String username, String oldPassword, String newPassword) throws Exception {
		boolean success = false;

		if (currentUser.getStatus() == UserStatus.GUEST) { // if registered in another running of client
			if (server.isUserRegistered(username, oldPassword) && server.getUsers().get(username).getPassword()
					.equals(PasswordCryptor.encryptToHex(oldPassword))) {
				currentUser = server.getUsers().get(username); // so it gets the info
			}
		}

		if (currentUser.getStatus() == UserStatus.REGISTERED || currentUser.getStatus() == UserStatus.LOGGED) {
			currentUser.setPassword(PasswordCryptor.encryptToHex(newPassword));
			server.updateUser(username, currentUser);
			success = true;
			messageSender.println(ConnectionMessages.PASS_RESEST_SUCCESS.getMessage());
		}
		if (success == false) {
			messageSender.println(ConnectionMessages.PASS_RESET_FAIL.getMessage());
		}
	}

	final static int THREE_CASE_INDEX = 3;
	final static int FOUR_CASE_INDEX = 4;
	final static int FIVE_CASE_INDEX = 5;
	final static int SIX_CASE_INDEX = 6;

	@Override
	public void updateUser(String[] splittedCommand) {
		boolean successfulUpdate = false;

		if (currentUser.getStatus() == UserStatus.LOGGED) {
			String oldUsername = currentUser.getUsername();
			if (server.isSessionActive(oldUsername)) {

				successfulUpdate = true;
				switch (splittedCommand.length) {
				case THREE_CASE_INDEX: {
					String username = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
					currentUser.setUsername(username);
					break;
				}
				case FOUR_CASE_INDEX: {
					String username = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
					String firstName = splittedCommand[ArrayIndexes.INDEX_3.getValue()];
					currentUser.setUsername(username);
					currentUser.setFirstname(firstName);
					break;
				}
				case FIVE_CASE_INDEX: {
					String username = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
					String firstName = splittedCommand[ArrayIndexes.INDEX_3.getValue()];
					String lastName = splittedCommand[ArrayIndexes.INDEX_4.getValue()];
					currentUser.setUsername(username);
					currentUser.setFirstname(firstName);
					currentUser.setLastname(lastName);
					break;
				}
				case SIX_CASE_INDEX: {
					String username = splittedCommand[ArrayIndexes.INDEX_2.getValue()];
					String firstName = splittedCommand[ArrayIndexes.INDEX_3.getValue()];
					String lastName = splittedCommand[ArrayIndexes.INDEX_4.getValue()];
					String email = splittedCommand[ArrayIndexes.INDEX_5.getValue()];
					currentUser.setUsername(username);
					currentUser.setFirstname(firstName);
					currentUser.setLastname(lastName);
					currentUser.setEmail(email);
					break;
				}
				}

			}
			if (successfulUpdate == true) {
				server.updateUser(oldUsername, currentUser);
				messageSender.println(ConnectionMessages.UPDATE_SUCCESS.getMessage());

			} else {
				messageSender.println(ConnectionMessages.UPDATE_FAIL.getMessage());
			}
		} else {
			messageSender.println(ConnectionMessages.LOG_IN_TO_UPDATE.getMessage());
		}
	}

	@Override
	public void logout(int sessionId) {

		if (currentUser.getStatus() == UserStatus.LOGGED) {
			if (server.canLogout(sessionId) == true) {
				currentUser.setStatus(UserStatus.REGISTERED);
				messageSender.println(ConnectionMessages.LOG_OUT_SUCCESS.getMessage());
			} else {
				messageSender.println(ConnectionMessages.LOG_OUT_FAIL.getMessage());
			}
		} else {
			messageSender.println(ConnectionMessages.LOG_IN_TO_LOG_OUT.getMessage());
		}
	}

	@Override
	public void deleteUser(String username) {
		if (currentUser.getStatus() == UserStatus.LOGGED) {
			currentUser = new User("guest");

			server.deleteUser(username);

			messageSender.println(ConnectionMessages.DELETE_SUCCESS.getMessage());
		} else {
			messageSender.println(ConnectionMessages.LOG_IN_TO_DELETE.getMessage());
		}

	}

}
