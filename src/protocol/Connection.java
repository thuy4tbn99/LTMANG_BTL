package protocol;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
		
           
			socketWrite = new ObjectOutputStream(socketConnection.getOutputStream());
            System.out.println("Open Output Stream");
            
        
            socketRead = new ObjectInputStream(socketConnection.getInputStream());
            System.out.println("Open Input Stream");
			//sendData;  
            receiveData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		// gui du lieu o day
		// Du lieu se la dang Packet
	}
	public void receiveData()
	{
		try 
		{
			packet = (Packet) socketRead.readObject();
			System.out.println("Message type: " + packet.getMsgType().toString());
			System.out.println("Message length: " + packet.getDataLength());
			System.out.println("Message data: " + new String(packet.getData(), StandardCharsets.UTF_8));
			
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
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
