package server;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
	public static Server server; 
	
	public static void main(String[] args) throws IOException {
		server = new Server(8000);
		
		Server.Init();

		Server.CreateRoom("sala 1", InetAddress.getLocalHost());
		Server.CreateRoom("sala 2", InetAddress.getLocalHost());
		Server.CreateRoom("sala 3", InetAddress.getLocalHost());
		
		System.out.println(Server.getRoomList());
	}
}
