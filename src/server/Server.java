package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.Connection;

public class Server extends Thread {

    private ServerSocket serverSocket = null;
    private ArrayList<Connection_with_ip> serverConnection = new ArrayList<Connection_with_ip>(); // quan ly cac ket noi tu client

	public ArrayList<Connection_with_ip> getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(ArrayList<Connection_with_ip> serverConnection) {
		this.serverConnection = serverConnection;
	}

	private static String fileName;
    private static final Object lock = new Object();
    
    public static String getFileName() {
        return fileName;
    }
    
    public static Object getLock() {
        return lock;
    }

    public Server(int number_of_connect, int port) {
        System.out.println("Server has been started!");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Currently listening on port "+port);
        synchronized (lock) {
            BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // Them ket noi moi vao 1 arrayList
                Socket acceptedSocket = null;
                try {
                	// them connection moi vao array cung voi ip cua client ket noi 
                    acceptedSocket = serverSocket.accept();
                    Connection newConn = new Connection(acceptedSocket, "Server", this);
                    String ip_conn = acceptedSocket.getInetAddress().toString();
                    serverConnection.add(new Connection_with_ip(newConn, ip_conn));
                    
                    System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " + acceptedSocket.getPort());
                    if (serverConnection.size() == number_of_connect) {
                    	System.out.print("Type file name you want to upload: ");
                        try {
                            fileName = cmdReader.readLine(); // doc ten file de chuan bi truyen file sang client
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(fileName);
                        serverConnection.get(0).getConnection().serverSendFile();;
                        break;
                    }
                    System.out.println("waiting for client ");
                } catch (IOException e) {
                    System.out.println("Cannot established a connection with the client.");
                    continue;
                }
            }
//            System.out.print("Type file name you want to upload: ");
//            try {
//                fileName = cmdReader.readLine(); // doc ten file de chuan bi truyen file sang client
//            } catch (IOException ex) {
//                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            System.out.println(fileName);
        }

    }

    public static void main(String[] args) {
    	DataInputStream input = new DataInputStream(System.in);
    	System.out.println("maximum number of connections : ");
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

