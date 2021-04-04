package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleServer {
	ServerSocket server = null; // наша сторона
	Socket client = null; // удаленная (remote) сторона
	DataInputStream in;
	DataOutputStream out;

	Scanner scanner;
	String message;

	public ConsoleServer() {

		scanner = new Scanner(System.in);

		try {
			server = new ServerSocket(6001);
			System.out.println("Server started");

			client = server.accept();
			System.out.printf("Client [%s] connected\n", client.getInetAddress());

			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());

			// поток ввода (чтения)
			new Thread(() -> {
				String str;
				try {
					while (true) {
						str = in.readUTF();
						if ("/end".equals(str)) {
							sendMessage("/Disconnect");
							System.out.printf("Client [%s] disconnected\n", client.getInetAddress());
							break;
						}
						sendMessage(String.format("Client [%s]: %s", client.getInetAddress(), str));
						System.out.printf("Client [%s]: %s\n", client.getInetAddress(), str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

			// читаем  строку из консоли и отправляем клиенту
			while (true) {
				message = scanner.nextLine();
				if ("/end".equals(message)) {
					break;
				}
				sendMessage("Server: " + message);
				System.out.println("Server: " + message);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMessage(String s) {
		try {
			out.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}