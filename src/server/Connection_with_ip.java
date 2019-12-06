package server;

import protocol.Connection;

public class Connection_with_ip{
	Connection connection = null;
	String ip = null;
	
	public Connection_with_ip(Connection connection, String ip) {
		this.connection = connection;
		this.ip = ip;
	}
	public Connection getConnection() {
		return connection;
	}
	public String getIp() {
		return ip;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
		
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}