package client;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import protocol.*;
import server.Server;

public class Client extends Thread {

    private Connection conn = null;

    public Client(String ip, int port) {
        try {
            // Tao 1 thread de ket noi den server
            // Xem cach de nghe ket noi cua cac thang clients
            conn = new Connection(new Socket(ip, port), "Client", null);
            System.out.println("Connected to the server!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("127.0.0.1", 2345);
    }

}
