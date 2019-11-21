package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocol.Connection;

public class server {
	
	private ServerSocket serverSocket = null;
	private ArrayList<Connection> serverConnection = new ArrayList<Connection>(); // quan ly cac ket noi tu client
	
	public server() throws IOException {
		while (true) {
			// them ket noi moi vao 1 arrayList
			serverSocket = new ServerSocket(2345);
			Socket acceptedSocket = serverSocket.accept();
			serverConnection.add(new Connection(acceptedSocket));	
		}
	}
	
	public static void main(String[] args) {
		try {
			new server();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
