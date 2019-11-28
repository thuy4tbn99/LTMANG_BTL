package protocol;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends Thread {
	
	private Socket socketConnection 		= null;
	private ObjectOutputStream socketWrite 	= null;
	private ObjectInputStream socketRead 	= null;
	private FileOutputStream fileWrite 		= null;
	private FileInputStream fileRead  		= null;
	private Packet packet;
	
	public Connection(Socket sv) {
		socketConnection = sv;
		start();
	}
		
	public void run() {
		try {
            System.out.println("Thread created!");
			// Nếu tạo 2 biến dưới thì không in ra cái ở hàm sendDatatoClient
           
			socketWrite = new ObjectOutputStream(socketConnection.getOutputStream());
            System.out.println("Open Output Stream");
            
            // bên client chưa tạo connect đến nên getInputStream bị hold cho đến khi có stream từ client
            socketRead = new ObjectInputStream(socketConnection.getInputStream());
            System.out.println("Open Input Stream");
			//sendDatatoClient();         
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendDatatoClient() {
		// gui du lieu o day
		// Du lieu se la dang Packet
	}

	public Socket getSocketConnection()
	{
		return socketConnection;
	}

	public void setSocketConnection(Socket socketConnection)
	{
		this.socketConnection = socketConnection;
	}

	public Packet getPacket()
	{
		return packet;
	}

	public void setPacket(Packet packet)
	{
		this.packet = packet;
	}
}
