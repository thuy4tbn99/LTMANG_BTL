package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.net.ServerSocket;

import protocol.*;

public class Client extends Thread {

    private Connection conn = null;
    private ServerSocket clientHost = null;
    // De ntn de test local, sang luc chay thi de la 2345
    private int clientPort = Math.abs(new Random(System.currentTimeMillis()).nextInt(65535));
    public Client(String ip, int port) {
        try {
            // Tao 1 thread de ket noi den server
            // Xem cach de nghe ket noi cua cac thang clients
            conn = new Connection(new Socket(ip, port), true, this.clientPort);
            System.out.println("Connected to the server!");
            
            while (true)
            {
            	clientHost = new ServerSocket(this.clientPort);
            	System.out.println("Locally hosting on the port " + this.clientPort);
            	conn = new Connection(clientHost.accept(), false);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Client("127.0.0.1", 2345);
    }

}
