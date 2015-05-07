package server;

import java.net.InetAddress;
import java.util.HashMap;

public class Server {

	private static HashMap<String, InetAddress> rooms;

	public static void main(String[] args) {
		rooms = new HashMap<String, InetAddress>();
	}

}
