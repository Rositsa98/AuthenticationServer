package bg.sofia.uni.fmi.mjt.authentication.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRunnable implements Runnable {

	private Socket clientSocket;

	ClientRunnable(Socket socket) {
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader commandReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			while (true) {
				if (clientSocket.isClosed()) {
					System.out.println("Client socket is closed, stop waiting for server messages");
					return;
				}
				String command = commandReader.readLine();
				if (command != null) {
					System.out.println(command);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException thrown when reading client command.");
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("IOException thrown when closing client socket.");
				e.printStackTrace();
			}
		}
	}

}