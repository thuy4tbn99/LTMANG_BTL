package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocol.Connection;

public class Server {
	
	// Danh sách chứa các kết nối đến
	private ArrayList<Connection> serverConnection = new ArrayList<Connection>();
	public Server()
	{
		try 
		{
			// Socket của server là cố định
			final ServerSocket serverSocket = new ServerSocket(5555);
			while (true)
			{
				Socket acceptedSocket = serverSocket.accept();
				serverConnection.add(new Connection(acceptedSocket));
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Server();
	}
	
}
