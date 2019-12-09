package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.Connection;

public class Server extends Thread {

    private ServerSocket serverSocket = null;
    private ArrayList<Connection> serverConnection = new ArrayList<Connection>(); // quan ly cac ket noi tu client

    private String clientHostIP;
    private int clientHostPort;
    
	private static String fileName;
    private static final Object lock = new Object();
    
    
    
    public ArrayList<Connection> getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(ArrayList<Connection> serverConnection) {
		this.serverConnection = serverConnection;
	}

	public static String getFileName() {
        return fileName;
    }
    
    public static Object getLock() {
        return lock;
    }

    public String getClientHostIP()
	{
		return clientHostIP;
	}

	public static void setClientHostIP(String clientHostIP)
	{
		clientHostIP = clientHostIP;
	}

	public int getClientHostPort()
	{
		return clientHostPort;
	}

	public void setClientHostPort(int clientHostPort)
	{
		clientHostPort = clientHostPort;
	}

	public Server(int number_of_connect, int port) {
        System.out.println("Server has been started!");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Currently listening on port " + port);
        synchronized (lock) {
            BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // Them ket noi moi vao 1 arrayList
                Socket acceptedSocket = null;
                try {
                	// them connection moi vao array cung voi ip cua client ket noi 
                    acceptedSocket = serverSocket.accept();
                    serverConnection.add(new Connection(acceptedSocket, false, this));
                    

                    System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " + acceptedSocket.getPort());
                    if (serverConnection.size() == number_of_connect) {
                    	System.out.print("Type file name you want to upload: ");
                        try {
                            fileName = cmdReader.readLine(); // doc ten file de chuan bi truyen file sang client
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                           
                        String ip_host = serverConnection.get(0).getSocketConnection().getInetAddress().toString().substring(1);
                        int port_host = serverConnection.get(0).getSocketConnection().getPort();
                        
                        serverConnection.get(0).serverSendFile(true, Server.getFileName(), ip_host);
                        
			System.out.print("Type file name you want to upload: ");
			fileName = cmdReader.readLine(); // doc ten file de chuan bi truyen file sang client
			serverConnection.get(0).serverSendFile(true, Server.getFileName(), ip_host);
                        
                        // chuyen sang cuoi cung cua sendFile();
                        
//                        for (int i = 1; i < serverConnection.size(); i++)
//                        {
//                        	serverConnection.get(i).serverRedirect();
//                        }
                    }
                    System.out.println("waiting for client ");
                } catch (IOException e) {
                    System.out.println("Cannot established a connection with the client.");
                    continue;
                }
            }
        }

    }

    public static void main(String[] args) {
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Enter the maximum number of connections : ");
    	int max_connect = 0;
    	try {
			max_connect = Integer.parseInt(input.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println(max_connect);
    	new Server(max_connect, 2345);
    }

}

