package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.net.ServerSocket;

import protocol.*;

public class Client extends Thread {

    private Connection conn = null;
    private ServerSocket clientHost = null;
    // De ntn de test local, sang luc chay thi de la 2345
    private int clientPort = Math.abs(new Random(System.currentTimeMillis()).nextInt(65535));
    
    protected static String fileName = null;
    
    protected boolean host = true;
    
    Socket acceptedSocket = null;
    
    public Client(String ip, int port) {
        try {
            // Tao 1 thread de ket noi den server
            // Xem cach de nghe ket noi cua cac thang clients
            conn = new Connection(new Socket(ip, port), true, this.clientPort, this);
            System.out.println("Connected to the server!");
            
            
            clientHost = new ServerSocket(this.clientPort);
            System.out.println("Locally hosting on the port " + this.clientPort);
            while (true)
            {
            	acceptedSocket = clientHost.accept();
            	conn = new Connection(acceptedSocket, false, host, getFileName());
            	System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " + acceptedSocket.getPort());
            	System.out.println("file name " + getFileName());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectToHost(String IP, int port, String fileName) {
    	System.out.println(" \"host ip: " + IP + " host port : "+ port );
    	System.out.println("file name " + fileName + "\"" );
    	try {
			conn = new Connection(new Socket(IP,port), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    public Client getClient() {
    	return this;
    }
    
    public void newConnection(String IP, int port) {
    	System.out.println("ds");
    }
    
    
    public static String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static void main(String[] args) {
        new Client("127.0.0.1", 2345);
    }

}
