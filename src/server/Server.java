package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocol.Connection;

public class Server {
	
	private ServerSocket serverSocket = null;
	private ArrayList<Connection> serverConnection = new ArrayList<Connection>(); // quan ly cac ket noi tu client
	
	public Server() {
		System.out.println("Server has been started!");
		try {
			serverSocket = new ServerSocket(2345);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Currently listening on port 2345.....");
		
		while (true) {
			// Them ket noi moi vao 1 arrayList
			
			Socket acceptedSocket = null;
			try {
				acceptedSocket = serverSocket.accept();
				Connection newConn = new Connection(acceptedSocket);
				serverConnection.add(newConn);	
				
				System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " +  acceptedSocket.getPort());
				
				// Bat dau trao doi du lieu
				// serverTalk ben Connection.java
				newConn.serverTalk();
				
			} catch (IOException e) {
				System.out.println("Cannot established a connection with the client.");
				continue;
			}
			
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
}
