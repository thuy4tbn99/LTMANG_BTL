package protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public Connection(Socket sv, boolean client) {
        this.socketConnection = sv;
        this.client = client;
        start();
    }

    public void run() {
        try {
            //System.out.println("A thread has been created!");

            socketWrite = new ObjectOutputStream(socketConnection.getOutputStream());
            socketRead = new ObjectInputStream(socketConnection.getInputStream());
            if(client) clientTalk();
            else serverTalk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clientTalk() {
        while (true) {
            try {
                // Doc yeu cau cua nguoi dung
                BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("What do you want to do?");
                System.out.println("Type LIST if you want to see the files available on the server, GET <space> fileName to download the file. ");
                System.out.print("Your request: ");
                String request = cmdReader.readLine().toLowerCase().trim();
                if (request.contains("list")) {
                    packet = new Packet(Message.LISTFILE, 0, null);
                    socketWrite.writeObject(packet);
                    System.out.println("Sending file list request...");
                    // Doc ten cac file tu server
                    packet = (Packet) socketRead.readObject();
                    System.out.println("List of files: ");
                    System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));
                } 
                else if (request.contains("get")) {
                    String[] tokens = request.split("\\s+");
                    // Gui request
                    packet = new Packet(Message.SENDFILE, tokens[1].length(), tokens[1].getBytes());
                    socketWrite.writeObject(packet);
                    client = true;
                    // Khong nhan duoc het file, file cu bi thieu du lieu
                    // Kiem tra loi o cho RECV vs SEND
                    serverTalk();
                } 
                else {
                    System.out.println("Cannot understand your request. Try again.");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void serverTalk() {
        while (true) {
            synchronized(Server.getLock())
            {
                System.out.println("Thread: " + Server.getFileName());
                
            }
            try {
                packet = (Packet) socketRead.readObject();
                System.out.println("Received a packet from a client.");

                switch (packet.getMsgType()) {
                    // Xu li cac kieu cua tin nhan
                    case NOTI: {
                        // In tin nhan duoc gui ra man hinh
                        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));
                        break;
                    }
                    case SENDFILE: {
                        try {
                            // Ten file se duoc gui trong phan data duoi dang string
                            file = new File("server/files/" + new String(packet.getData(), StandardCharsets.UTF_8));
                            fileRead = new FileInputStream(file);
                            long fileSize = file.length();
                            System.out.println("Received a request to send file " + file.getName() + " to the client. The file size is: " + fileSize + " bytes.");

                            // Doc va gui file cho nguoi request
                            socketWrite.writeObject(new Packet(Message.RECVFILE, file.getName().getBytes().length, file.getName().getBytes()));

                            /* Nhet cai file size vao trong packet???
							 * Chuyen tu long sang byte hay de ntn?
							 * Co cai nay cua google ma phai add jar vao 
							 * https://github.com/google/guava/wiki/PrimitivesExplained#byte-conversion-methods
							 * Prim la kieu long, int, ....
                             */
                            socketWrite.writeLong(fileSize);
                            int count = 0;

                            // Doc roi gui nhu binh thuong
                            // Kiem tra lai doan duoi, hinh nhu data bi mat
                            while (fileSize > count) {         
                            	packet = new Packet();
                                int reading_size = fileRead.read(packet.getData());
                                packet.setDataLength(reading_size);
                                socketWrite.writeObject(packet);
                                count += reading_size;
                            }
                            System.out.println("File sent to the client!");
                            fileRead.close();
                        } catch (IOException e) {
                            String error = "Cannot find or open the file you requested.";
                            socketWrite.writeObject(new Packet(Message.NOTI, error.getBytes().length, error.getBytes()));
                        }
                        break;
                    }
                    case RECVFILE: {
                        try {
                            long begin = System.currentTimeMillis();
                        	file = new File("client/files/" + new String(packet.getData(), StandardCharsets.UTF_8));
                            fileWrite = new FileOutputStream(file);
                            

                            // Hien tai chua chuyen duoc long sang byte nen khong dung packet
                            long fileSize = socketRead.readLong();
                            System.out.println("File size is: " + fileSize + " bytes.");
                            int count;

                            // Doc du lieu roi viet vao file
                            // Dung luong giong nhau nhung file thi khong giong
                            while (fileSize > 0) {
                                packet = (Packet) socketRead.readObject();
                                count = packet.getDataLength();
                                fileWrite.write(packet.getData(), 0, count);
                                fileSize -= count;
                            }
                            System.out.println("File received!");
                            fileWrite.close();
                            System.out.println(System.currentTimeMillis() - begin);
                            if (client) {
                                return;
                            } else {
                                break;
                            }
                        } catch (ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case LISTFILE: {
                        // Liet ke danh sach file trong folder
                        File folder = new File("server/files");
                        File[] fileNames = folder.listFiles();
                        if (fileNames.length == 0) {
                            String message = "No file exist in the server folder.";
                            socketWrite.writeObject(new Packet(Message.NOTI, message.getBytes().length, message.getBytes()));
                            break;
                        }

                        // Tao string chua ten cac file trong folder do
                        StringBuilder message = new StringBuilder();
                        for (int i = 0; i < fileNames.length; i++) {
                            message.append(fileNames[i].getName() + "\t" + fileNames[i].length() + " bytes\n");
                        }

                        // E F F I C I E N C Y
                        message.trimToSize();
                        socketWrite.writeObject(new Packet(Message.NOTI, message.toString().getBytes().length, message.toString().getBytes()));
                        System.out.println("Sending list of files.");
                        break;
                    }
                    default: {
                        System.out.println("Connection terminated!");
                        break;
                    }
                }
            } 
            catch (IOException | ClassNotFoundException | NullPointerException e) {
                /* Doc packet lien tuc tu socket nen no bi NullPointerEx vi client chua gui gi ca
				 * Co cach nao tot hon k??
                 */
                continue;
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
