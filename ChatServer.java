package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * SASHA SMOLYANSKY
 */

public class ChatServer extends ChatWindow {

	// Vector used to store active clients
	static Vector<ClientHandler> clients = new Vector<>();
	// counter for clients
	static int clientCounter = 0;

	public ChatServer() {
		super();
		this.setTitle("Chat Server");
		printMsg("Server Started");
		this.setLocation(80, 80);

		try {
			// Create a listening service for connections
			// at the designated port number.
			ServerSocket srv = new ServerSocket(2113);
			while (true) {
				Socket socket = srv.accept();
				ClientHandler handler = new ClientHandler(socket, "Client Number " + clientCounter);
				Thread t = new Thread(handler);
				clients.add(handler);
				t.start();
				// keeping track of the number of clients joining the server
				clientCounter++;
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * This inner class handles
	 * communication to/from one client.
	 */
	class ClientHandler implements Runnable {
		private PrintWriter writer;
		private BufferedReader reader;

		public String clientName;
		// checking for first connection (name) from clients
		boolean isThisClientName = true;
		Socket socket;

		public ClientHandler(Socket socket, String name) {
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection from " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				clientName = name;
			} catch (IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
			this.socket = socket;
		}

		public void run() {
			try {
				// first string = message
				clientName = readMsg(clientName);
				while (true) {
					// read a message from the client
					String sr = readMsg(clientName);
					int inputLength = sr.length();
					if (inputLength >= 5 && sr.substring(0, 5).compareTo("/name") == 0) {
						// works if submitted in format /name_Sasha
						// space must be included otherwise name change will be short the first letter
						String newName = sr.substring(6, inputLength);
						displayMsg(clientName + " changed their name to " + newName, clientName);
						clientName = newName;
					} else {
						displayMsg(sr, clientName);
					}
				}
			// attempting to display that the client has left the chat
			} catch (IOException e) {
				printMsg(this.clientName + " has left the chat\n");
				ChatServer.this.removeClient(this);
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		/**
		 * Receive and display a message
		 */
		public String readMsg(String clientName) throws IOException {
			String s = reader.readLine();
			if (isThisClientName) {
				printMsg(clientName + " (" + s + ") has joined.");
				isThisClientName = false;
			} else {
				printMsg(clientName + " sent " + s);
			}
			return s;
		}

		/**
		 * Send a string
		 */
		// sending client's message to client chat
		public void sendMsg(String s, String clientName) {
			writer.println(clientName + ": " + s);
		}
	}

	// display all the clients messages in the Server
	private void displayMsg(String s, String clientName) {
		for (ClientHandler handle : clients) {
			handle.sendMsg(s, clientName);
		}
	}

	void removeClient(ClientHandler clientHandler) {
		for (int i = clients.size() - 1; i >= 0; i--) {
			if (clientHandler == clients.elementAt(i)) {
				clients.removeElementAt(i);
				break;
			}
		}
		displayMsg(clientHandler.clientName + " has left the chat", "Server");
	}

	public static void main(String args[]) {
		new ChatServer();
	}
}