package l01;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Client {
	String host_name;
	int port_number;
	DatagramSocket socket;
	
	public Client() throws IOException {
		host_name = new String();
		port_number = 4445;
		socket = new DatagramSocket();
	}
	
	void receive (DatagramPacket p) {
		
	}
	
	void send (DatagramPacket p) {
		
	}
}