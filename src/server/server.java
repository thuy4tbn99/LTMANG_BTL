package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocol.Connection;

public class server {
	
	private ServerSocket serverSocket = null;
	private ArrayList<Connection> serverConnection = new ArrayList<Connection>(); // quan ly cac ket noi tu client
	
	public server() {
		try {
			serverSocket = new ServerSocket(2345);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			// them ket noi moi vao 1 arrayList
			
			Socket acceptedSocket = null;
			try {
				acceptedSocket = serverSocket.accept();
				Connection newCont = new Connection(acceptedSocket);
				serverConnection.add(newCont);	
			} catch (IOException e) {
				System.out.println("client closed");
				continue;
			}
			
		}
	}
	
	public static void main(String[] args) {
		new server();
	}
	
}
