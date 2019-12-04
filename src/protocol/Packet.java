package protocol;

import java.io.Serializable;

public class Packet implements Serializable {
    // Cau truc packet don gian

    private Message msgType;
    private int dataLength;
    private byte[] data = new byte[1024];

    public Packet(Message msgType, int dataLength, byte[] data) {
        this.msgType = msgType;
        this.dataLength = dataLength;
        this.data = data;
    }

    public Packet() {
        this.msgType = null;
    }

    public Message getMsgType() {
        return msgType;
    }

    public void setMsgType(Message msgType) {
        this.msgType = msgType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
