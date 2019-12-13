package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Queue;


import protocol.*;

public class Client extends Thread {

    private Connection conn = null;
    private ServerSocket clientHost = null;
    // De ntn de test local, sang luc chay thi de la 2345
    //private int clientPort = Math.abs(new Random(System.currentTimeMillis()).nextInt(65535));
    private int clientPort = 2346;
    private static final Object QUEUE_LOCK = new Object();
    
    // Queue packet tu serv de forward cho client
    private Queue<Packet> queue = new LinkedList<>();
    private int number_of_clients = 0;
    // so lan dc doc cua 1st Ele trong queue
    // 
    private int firstEleRead = 0;
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
            while (true) {
                acceptedSocket = clientHost.accept();
                conn = new Connection(acceptedSocket, false, host, this);
                number_of_clients++;
                System.out.println("Connection established with a client, IP: " + acceptedSocket.getInetAddress() + ", port: " + acceptedSocket.getPort());
                System.out.println("File name " + getFileName());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void connectToHost(String IP, int port,long fileSize, String fileName, Client c) {
        System.out.println("Host ip: " + IP + ", host port: " + port);
        System.out.println("File name: " + fileName + ", file size: " + fileSize);
        try {
            conn = new Connection(new Socket(IP, port), true, true, this, fileSize, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client getClient() {
        return this;
    }
    
    public static Object getLock() {
        return Client.QUEUE_LOCK;
    }

    public Queue<Packet> getQueue() {
        return queue;
    }
    
    
    public int getNumberOfClients() {
        return number_of_clients;
    }
    
    public void changeClientNum(int amount)
    {
    	number_of_clients -= amount;
    }
    
    public void EleRead() {
        firstEleRead++;
        if (firstEleRead == number_of_clients) {
            queue.remove();
            firstEleRead = 0;
        }
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
        new Client("192.168.1.10", 2345);
    }

}
