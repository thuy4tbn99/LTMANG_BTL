package client;

import java.io.IOException;
import java.net.Socket;

import protocol.*;


public class Client {
 	private Connection conn = null;
	
    public Client(String ip, int port)
    {
        try
        {
        	// Tao 1 thread de ket noi den server
        	// Xem cach de nghe ket noi cua cac thang client
            conn = new Connection(new Socket(ip, port));
            System.out.println("Connected to the server!");
            
            // Bat dau trao doi du lieu
            // clientTalk ben Connection.java
            conn.clientTalk();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
    	new Client("127.0.0.1", 2345);
    }
    
}
