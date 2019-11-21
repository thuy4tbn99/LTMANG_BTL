package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class server {
	
	protected ServerSocket server_sock = null;
	ArrayList<Connection> serConnection = new ArrayList<Connection>(); // quan ly cac connection tu client
	
	public server() throws IOException{
		while (true) {
			// them ket noi moi vao 1 arrayList
			server_sock = new ServerSocket(2345);
			Socket sock = server_sock.accept();
			serConnection.add(new Connection(sock));
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
