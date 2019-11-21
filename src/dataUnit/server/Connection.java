package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends Thread {
	
	Socket sock_connection = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois  = null;
	FileInputStream fis    = null;
	
	public Connection(Socket sv) {
		sock_connection = sv;
	}
	public void run() {
		try {
			oos = new ObjectOutputStream(sock_connection.getOutputStream());
			ois = new ObjectInputStream(sock_connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendDatatoClient();
	}
	
	public void sendDatatoClient() {
		// gui du lieu o day
	}

}
