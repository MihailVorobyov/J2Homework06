package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	private final String ADDRESS = "localhost";
	private final int PORT = 6001;

	private Scanner scanner;

	public ChatClient () {

		scanner = new Scanner(System.in);

		try {
			socket = new Socket(ADDRESS, PORT);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			// поток ввода
			new Thread(() -> {
				String messageFromServer;
				try {
					while (true) {
						messageFromServer = in.readUTF();
						if ("/Disconnect".equals(messageFromServer)) {
							break;
						}
						System.out.println(messageFromServer);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();

			// поток вывода
			String message;

			while (true) {
				try {
					message = scanner.nextLine();
					out.writeUTF(message);
						if ("/end".equals(message)) {
							break;
						}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Disconnect");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
