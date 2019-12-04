package protocol;

import java.io.Serializable;

public class Packet implements Serializable {
    // Cau truc packet don gian

    private Message msgType;
    private long dataLength;
    private byte[] data = new byte[1024];

    public Packet(Message msgType, long dataLength, byte[] data) {
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

    public long getDataLength() {
        return dataLength;
    }

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
