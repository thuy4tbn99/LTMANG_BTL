package protocol;

import java.io.Serializable;

public class Packet implements Serializable {
	
	// Cấu trúc packet cơ bản
	// TODO Bổ sung các loại tin khác
	private Message msgType;
	private long dataLength;
	private long offset;
	private byte[] data;
	
	public Packet(Message msgType, long dataLength, long offset, byte[] data)
	{
		this.msgType = msgType;
		this.offset = offset;
		this.dataLength = dataLength;
		this.data = data;
	}
	
	public Packet() {}

	public Message getMsgType() 
	{
		return msgType;
	}

	public void setMsgType(Message msgType) 
	{
		this.msgType = msgType;
	}

	public long getDataLength() 
	{
		return dataLength;
	}

	public void setDataLength(long dataLength) 
	{
		this.dataLength = dataLength;
	}

	public long getOffset() 
	{
		return offset;
	}

	public void setOffset(long offset) 
	{
		this.offset = offset;
	}

	public byte[] getData() 
	{
		return data;
	}

	public void setData(byte[] data) 
	{
		this.data = data;
	}
}

