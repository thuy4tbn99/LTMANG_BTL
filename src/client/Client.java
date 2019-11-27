package client;

import java.io.IOException;
import java.net.Socket;


public class Client {
    public Client()
    {
        try
        {
            Socket sock = new Socket("127.0.0.1", 5555);
            System.out.println("Connected.");
            doSomething();
            sock.close();
            
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    public void doSomething()
    {
        for(int i=0; i<10; i++)
        {
            System.out.println(i);
        }
    }
    
    public static void main()
    {
        Client Cli = new Client();
        
    }
    
}
