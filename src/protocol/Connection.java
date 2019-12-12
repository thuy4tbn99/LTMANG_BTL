package protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import server.Server;
import client.Client;

public class Connection extends Thread {

    private Socket socketConnection = null;
    private ObjectOutputStream socketWrite = null;
    private ObjectInputStream socketRead = null;
    private FileOutputStream fileWrite = null;
    private FileInputStream fileRead = null;
    private Packet packet = null;
    private File file = null;
    private boolean client;
    private int clientHostPort;
    private Server server;
    private Client _client;

    private boolean host = false; // dung de check xem client co phai la host hay khong
    private boolean isClientToHost = false; // check ket noi tu client den Host
    private String FileName_Host = null;

    public Connection(Socket sv, boolean client, Server server) {
        this.socketConnection = sv;
        this.client = client;
        this.server = server;
        start();
    }

    public Connection(Socket sv, boolean client) {
        this.socketConnection = sv;
        this.client = client;
        start();
    }
    
    // Connection cua client den Server, Client gui kem port ma minh dang mo Serv
    public Connection(Socket sv, boolean client, int clientHostPort, Client _client) {
        this.socketConnection = sv;
        this.client = client;
        this.clientHostPort = clientHostPort;
        this._client = _client;
        start();
    }
    
    // Connection cua client toi Host, xac dinh bang tham so isClientToHost
    public Connection(Socket sv, boolean client, boolean clientToHost, Client _client) {
        this.socketConnection = sv;
        this.client = client;
        this.isClientToHost = clientToHost;
        this._client = _client;
        start();
    }

    // Connection cua Host toi Client, xac dinh bang tham so host
    public Connection(Socket sv, boolean client, boolean host, String FileName) {
        this.socketConnection = sv;
        this.client = client;
        this.host = host;
        this.FileName_Host = FileName;
        start();
    }

    public void run() {
        try {
            System.out.println("A thread has been created!");

            socketWrite = new ObjectOutputStream(socketConnection.getOutputStream());
            socketRead = new ObjectInputStream(socketConnection.getInputStream());
            if (socketWrite == null || socketRead == null) {
                System.out.println("socket read write null");
            }
            if (host) {
                this.serverSendFile(false, FileName_Host, null);
            }
            if (client) {
                ClientTalk();
                if (isClientToHost) {
                    System.out.println("Thread close");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClientTalk() {
        // co recv file,  message, client_ip, redirect_connection
        while (true) {
            try {
                packet = (Packet) socketRead.readObject();
                System.out.println("Received a packet from a server.");
                System.out.println(packet.getMsgType());
                switch (packet.getMsgType()) {
                    case MESSAGE_SERVER: {
                        // In tin nhan duoc gui ra man hinh
                        String fromClient = new String(packet.getData(), StandardCharsets.UTF_8);
                        System.out.println(fromClient);
                        break;
                    }
                    case MESSAGE_CLIENT: {
                        // In tin nhan duoc gui ra man hinh
                        // khong co tac dung gi trong xu ly
                        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));
                        break;
                    }
                    case RECEIVE_FILE: {
                        long begin = System.currentTimeMillis();
                        String file_name = new String(packet.getData(), StandardCharsets.UTF_8);
                        file = new File("src/client/files/" + file_name);

                        fileWrite = new FileOutputStream(file);

                        _client.setFileName(file_name);
                        System.out.println("file Name: " + file_name);
                        long fileSize = socketRead.readLong();

                        System.out.println("File size is: " + fileSize + " bytes.");
                        int count;

                        // Doc du lieu roi viet vao file
                        while (fileSize > 0) {
                            packet = (Packet) socketRead.readObject();
                            count = packet.getDataLength();
                            fileWrite.write(packet.getData(), 0, count);
                            fileSize -= count;
                        }
                        System.out.println("File received!");
                        fileWrite.close();
                        System.out.println(System.currentTimeMillis() - begin);
//	                    // Gui tin nhan thong bao minh la nguoi co file
//	                    String message = socketConnection.getInetAddress().toString();
//	                    packet = new Packet(Message.REDIRECT_CONNECTION, message.getBytes().length, message.getBytes());
//	                    socketWrite.writeInt(clientHostPort);
//	                    socketWrite.writeObject(packet);

                        break;
                    }

                    case REDIRECT_CONNECTION: {
                        // nhan ip cua client chuan bi dong vai tro server
                        System.out.println("Redirecting connection to a client");
                        String fromClient = new String(packet.getData(), StandardCharsets.UTF_8);
                        String[] ip = fromClient.trim().split("@");
                        //System.out.print("host ip: " + ip[0] + "host port : "+ ip[1]);
                        String IP_host = ip[0];
                        int port = Integer.parseInt(ip[1]);
                        String FileName = ip[2];
                        get_client().connectToHost(IP_host, port, FileName, get_client());

                        break;
                    }
                    default: {
                        System.out.println("Connection terminated!");
                        break;
                    }
                }
                if (isClientToHost) {
                    System.out.println("Done receiving file, disconnecting...");
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    public void serverRedirect(String host_ip, String fileName) {
        try {
            // Redirecting other client to the client with the file...
            String ip_Host_port = host_ip + "@2346@" + fileName;

            Packet redirect_pack = new Packet(Message.REDIRECT_CONNECTION, ip_Host_port.getBytes().length, ip_Host_port.getBytes());
            socketWrite.writeObject(redirect_pack);

            System.out.println("Redirecting other clients...");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void serverSendFile(boolean redirect, String FileName, String ip_host) {
        // neu la fileServer truyen file thi true de chay doan code gui yeu cau redirect den cac client
        // neu la client_host thi false
        try {
            // Ten file se duoc gui trong phan data duoi dang string
            if (host) {
                file = new File("src/client/files/" + FileName);
            } else {
                file = new File("src/server/files/" + FileName);
            }

            System.out.println("src file: " + file.getAbsolutePath());
            fileRead = new FileInputStream(file);
            long fileSize = file.length();
            System.out.println("Sending file " + file.getName() + " to the client. The file size is: " + fileSize + " bytes.");

            if (socketWrite == null) {
                System.out.println("socket write null");
            }

            // gui ten file, kich huoc filte cho client
            socketWrite.writeObject(new Packet(Message.RECEIVE_FILE, file.getName().getBytes().length, file.getName().getBytes()));
            socketWrite.writeLong(fileSize);

            int count = 0;

            // doc va gui file theo packet cho client
            while (fileSize > count) {
                packet = new Packet();
                int reading_size = fileRead.read(packet.getData());
                packet.setDataLength(reading_size);
                socketWrite.writeObject(packet);
                count += reading_size;
            }
            System.out.println("File sent to the client!");
            fileRead.close();

            // Nhan tin nhan tu thang client muon host
            if (redirect) {

//	            int port = socketRead.readInt();
//	            //server.setClientHostPort(port);
//	            //System.out.println("host port: "+ port);
//	            
//	    		packet = (Packet) socketRead.readObject();
//	    		String host_ip = new String(packet.getData(), StandardCharsets.UTF_8).substring(1);
//	    		//server.setClientHostIP(host_ip);
//	    		//System.out.println("ip host: "+ host_ip );
                // gui ip+ port cua host sang cho cac client con lai
                System.out.println("iP host: " + ip_host);

                for (int i = 1; i < server.getServerConnection().size(); i++) {
                    server.getServerConnection().get(i).serverRedirect(ip_host, FileName);
                    System.out.println("sent redirect request to client...");
                }
            }
        } catch (Exception e) {
            String error = "Cannot find or open the file you requested.";
            try {
                socketWrite.writeObject(new Packet(Message.MESSAGE_CLIENT, error.getBytes().length, error.getBytes()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    public Socket getSocketConnection() {
        return socketConnection;
    }

    public void setSocketConnection(Socket socketConnection) {
        this.socketConnection = socketConnection;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Client get_client() {
        return _client;
    }

    public void set_client(Client _client) {
        this._client = _client;
    }

}
