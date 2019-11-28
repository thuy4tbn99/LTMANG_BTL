package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client {
	Socket sock = null;
	private ObjectOutputStream socketWrite 	= null;
	private ObjectInputStream socketRead 	= null;
	
    public Client(String ip, int port)
    {
        try
        {
            sock = new Socket(ip, port);
            System.out.println("Connected.");
            socketWrite = new ObjectOutputStream(sock.getOutputStream());
            
            while(true) {
            	//System.out.println("trong while client");
            }  
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
        	try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
    }
    
    public void doSomething()
    {
        System.out.println("do smt");
    }
    
    public static void main(String[] args)
    {
        Client Cli = new Client("127.0.0.1", 2345);
        
    }
    
}
