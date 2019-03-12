package bg.sofia.uni.fmi.mjt.authentication.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.authentication.server.ArrayIndexes;

public class Client {

	private PrintWriter commandSender;

	private void run() throws IOException {
		try (BufferedReader commandsReader = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				String clientCommand = commandsReader.readLine();

				String[] commandSplitted = clientCommand.split(" ");
				String initialCommand = commandSplitted[ArrayIndexes.INDEX_0.getValue()];
				if ("connect".equals(initialCommand)) {
					String serverHost = commandSplitted[ArrayIndexes.INDEX_1.getValue()];
					int serverPort = Integer.parseInt(commandSplitted[ArrayIndexes.INDEX_2.getValue()]);

					connect(serverHost, serverPort);
				} else {
					// when connection between client and server
					// is established commands are handled by
					// connection runnable class
					commandSender.println(clientCommand);
				}
			}
		} finally {
			commandSender.close();
		}
	}

	private void connect(String serverHost, int serverPort) {
		try {
			Socket serverSocket = new Socket(serverHost, serverPort);
			commandSender = new PrintWriter(serverSocket.getOutputStream(), true);

			System.out.println("successfully opened a socket");

			ClientRunnable clientRunnable = new ClientRunnable(serverSocket);
			new Thread(clientRunnable).start();
		} catch (IOException e) {
			System.out.println("=> Cannot connect to server on :" + serverHost + " " + serverPort
					+ ", make sure that the server is started");
		}
	}

	public static void main(String[] args) throws IOException {
		new Client().run();
	}

}
