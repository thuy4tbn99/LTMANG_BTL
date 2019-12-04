package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.Connection;

public class Server {

    private ServerSocket serverSocket = null;
    private ArrayList<Connection> serverConnection = new ArrayList<Connection>(); // quan ly cac ket noi tu client

    private static String fileName;
    private static final Object lock = new Object();
    
    public static String getFileName() {
        return fileName;
    }
    
    public static Object getLock() {
        return lock;
    }

    public Server() {
        System.out.println("Server has been started!");
        try {
            serverSocket = new ServerSocket(2345);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Currently listening on port 2345.....");
        synchronized (lock) {
            BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // Them ket noi moi vao 1 arrayList
                Socket acceptedSocket = null;
                try {
                    acceptedSocket = serverSocket.accept();
                    Connection newConn = new Connection(acceptedSocket, false);
                    serverConnection.add(newConn);
                    System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " + acceptedSocket.getPort());
                    if (serverConnection.size() == 1) {
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Cannot established a connection with the client.");
                    continue;
                }
            }
            System.out.print("Type file name you want to upload: ");
            try {
                fileName = cmdReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(fileName);
        }

    }

    public static void main(String[] args) {
        new Server();
    }

}
