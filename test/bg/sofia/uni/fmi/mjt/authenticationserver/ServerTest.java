package bg.sofia.uni.fmi.mjt.authenticationserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.authentication.client.User;
import bg.sofia.uni.fmi.mjt.authentication.connection.PasswordCryptor;
import bg.sofia.uni.fmi.mjt.authentication.connection.Session;
import bg.sofia.uni.fmi.mjt.authentication.server.ArrayIndexes;
import bg.sofia.uni.fmi.mjt.authentication.server.Server;

public class ServerTest {
	private static Server server;

	private final static int SAMPLE_TIME_TO_LIVE = 1000;

	@BeforeClass
	public static void setUp() {
		server = new Server();
	}

	@Test
	public void testIfRegisterUserWorksProperly() throws Exception {
		
		String username = "ivan";
		String password = "123";
		String name = "ivan";
		String lastName = "ivanov";
		String email = "ivan@gmail";
		User existingUserInFile = new User(username, PasswordCryptor.encryptToHex(password), name, lastName, email);

		Assert.assertFalse(server.canRegisterUser(existingUserInFile));
	}

	@Test
	public void testIfLogInWithUserNameWorksProperly() {
		String username = "asena";
		String password = "1234";

		Assert.assertEquals(server.logInWithUsername(username, password),
				server.getSessions().get(username).getId() + " " + server.getSessions().get(username).getTimeToLive());
	}

	@Test
	public void testIfLogInWithSessionIfWorksProperly() throws InterruptedException {
		// try to login with expired session
		int shouldReturnMinusOne = -1;
		String username = "asena";
		int sessionId = username.hashCode();
		Assert.assertEquals(server.logInWithSession(sessionId), shouldReturnMinusOne);

		String password = "1234";

		// does it get session time to live correctly
		server.logInWithUsername(username, password);
		Assert.assertEquals(server.logInWithSession(username.hashCode()),
				server.getSessions().get(username).getTimeToLive());

		Assert.assertTrue(server.isSessionActive(username));
	}

	@Test
	public void testIfLogOutWorksProperly() {
		// try to log out without been logged in
		Assert.assertFalse(server.canLogout("rosi".hashCode()));

		String username = "asena";
		String password = "1234";
		server.logInWithUsername(username, password);
		int sessionId = server.getSessions().get(username).getId();
		Assert.assertTrue(server.canLogout(sessionId));
	}

	@Test
	public void testIfUpdateUserWorksProperly() {
		String oldUsername = "asena";
		String password = "1234";
		String newName = "asi";
		String firstName = "Asena";
		String lastName = "Hristova";
		String email = "asenka2003@abv.bg ";

		server.updateUser(oldUsername, new User(newName, password, firstName, lastName, email));

		List<String> users = new ArrayList<>();

		String row = "";
		try (BufferedReader reader = new BufferedReader(new FileReader("usersData.txt"))) {
			while ((row = reader.readLine()) != null) {
				users.add(row.split(" ")[ArrayIndexes.INDEX_0.getValue()]);
			}
		} catch (IOException e) {
			System.out.println("IOException when reading usersData file in tests found.");
		}

		Assert.assertFalse(users.contains(oldUsername));
	}

	@Test
	public void testIfDeleteUserWorksProperly() throws IOException {
		String username = "abc";
		String password = "123";
		server.logInWithUsername(username, password);

		server.deleteUser(username);

		Assert.assertFalse(server.getUsers().get(username) != null);

		int sessionId = username.hashCode();
		Assert.assertFalse(server.getSessions().get(sessionId) != null);

	}

	@Test
	public void testIfCleanSessionsWorksProperly() {
		String testUsername = "testUser";
		Session testSession = new Session(testUsername, SAMPLE_TIME_TO_LIVE);
		server.getSessions().put(testUsername, testSession);

		Assert.assertTrue(server.getSessions().get(testUsername) != null);
		server.getSessions().get(testUsername).setTimeToLive(ArrayIndexes.INDEX_0.getValue());

		server.cleanSessions();

		Assert.assertTrue(server.getSessions().get(testUsername) == null);
	}

	@Test
	public void testIfThereIsSessionWithIdWorkingProperly() {
		String testUsername = "testUser";
		Session testSession = new Session(testUsername, SAMPLE_TIME_TO_LIVE);
		server.getSessions().put(testUsername, testSession);

		Assert.assertTrue(server.getSessions().get(testUsername) != null);
	}
}
