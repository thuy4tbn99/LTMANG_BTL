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
    
    public Connection(Socket sv, boolean client, int clientHostPort)
    {
    	this.socketConnection = sv;
        this.client = client;
        this.clientHostPort = clientHostPort;;
        start();
    }

    public void run() {
        try {
            //System.out.println("A thread has been created!");

            socketWrite = new ObjectOutputStream(socketConnection.getOutputStream());
            socketRead = new ObjectInputStream(socketConnection.getInputStream());
           	if (client) ClientTalk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ClientTalk() {
    	// co recv file,  message, client_ip, redirect_connection
    	while(true) {
	    	try {
	    		packet = (Packet) socketRead.readObject();
	            System.out.println("Received a packet from a server.");
				
		    	switch (packet.getMsgType()) {
		    		case MESSAGE_SERVER: {
		    			 // In tin nhan duoc gui ra man hinh
		    			String fromClient = new String(packet.getData(), StandardCharsets.UTF_8);
	                    System.out.println(fromClient);
	                    
	                    // lang nghe goi tin done tu client 
	                    // nhan tin nhan down file @@done@@ tu client
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
	                	file = new File("src/client/files/" + new String(packet.getData(), StandardCharsets.UTF_8));
	                    fileWrite = new FileOutputStream(file);
	
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
	                    
	                    // Gui tin nhan thong bao minh la nguoi co file
	                    String message = socketConnection.getInetAddress().toString();
	                    packet = new Packet(Message.REDIRECT_CONNECTION, message.getBytes().length, message.getBytes());
	                    socketWrite.writeInt(clientHostPort);
	                    socketWrite.writeObject(packet);
	                    
	                    break;
		    		}
		    		
		    		case REDIRECT_CONNECTION: {
		    			// nhan ip cua client chuan bi dong vai tro server
		    			System.out.println("Redirecting connection to a client");
		    			String fromClient = new String(packet.getData(), StandardCharsets.UTF_8);
		    			String[] ip = fromClient.trim().split("@");
		    			System.out.print("host ip: " + ip[0] + "host port : "+ ip[1]);
		    			
		    			
//		    			System.out.println(server.getClientHostIP());
//		    			System.out.println(server.getClientHostPort());
		    			//Connection redirect = new Connection(new Socket(server.getClientHostIP(), server.getClientHostPort()), true);
		    			break;
		    		}
		    		default:
		    		{
		    			System.out.println("Connection terminated!");
		    			break;
		    		}
		    	}
			} catch (Exception e) {
				continue;
			}
    	}
    }
    
    public void serverRedirect(String host_ip, int port)
    {
    	try {
			// Redirecting other client to the client with the file...
    		String ip_Host_port = host_ip + "@" + port;

    		Packet redirect_pack = new Packet(Message.REDIRECT_CONNECTION, ip_Host_port.getBytes().length, ip_Host_port.getBytes() );
    		socketWrite.writeObject(redirect_pack);
			
    		System.out.println("Redirecting other clients...");
		} 
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void serverSendFile() {
    	try {
            // Ten file se duoc gui trong phan data duoi dang string
            file = new File("src/server/files/" + new String(Server.getFileName()));
            fileRead = new FileInputStream(file);
            long fileSize = file.length();
            System.out.println("Sending file " + file.getName() + " to the client. The file size is: " + fileSize + " bytes.");

            // gui ten file, kich thuoc file cho client
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

            int port = socketRead.readInt();
            server.setClientHostPort(port);
            //System.out.println("host port: "+ port);
            
    		packet = (Packet) socketRead.readObject();
    		String host_ip = new String(packet.getData(), StandardCharsets.UTF_8).substring(1);
    		server.setClientHostIP(host_ip);
    		//System.out.println("ip host: "+ host_ip );
    		
    		// gui ip+ port cua host sang cho cac client con lai
    		
    		for( int i =1 ; i< server.getServerConnection().size(); i++) {
    			server.getServerConnection().get(i).serverRedirect(host_ip, port);
    			System.out.print(i+ " ");
    		}
    		
        } 
    	catch (ClassNotFoundException | IOException e) {
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
}
