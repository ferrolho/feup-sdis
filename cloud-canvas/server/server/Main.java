package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.net.httpserver.HttpServer;


public class Main {

	// map<roomName, room>
	private static HashMap<String, Room> rooms;

	public static void main(String[] args) throws IOException {
		rooms = new HashMap<String, Room>();
		 HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		   server.createContext("/canvas", new RequestHandler());
		   server.setExecutor(null); // creates a default executor
		   server.start();
		
		   create("sala 1",InetAddress.getLocalHost());
		   create("sala 2",InetAddress.getLocalHost());
		   create("sala 3",InetAddress.getLocalHost());
		
	}

	public static void create(String name, InetAddress ip) {
		if (!rooms.containsKey(name))
			rooms.put(name, new Room(ip));
		else
			System.err
					.println("Room name already exists. Room creation failed.");
	}

	public static void incUsers(String name) {
		rooms.get(name).incNumUsers();
	}

	public static void decUsers(String name) {
		Room room = rooms.get(name);

		room.decNumUsers();

		if (room.isEmpty())
			delete(name);
	}

	private static void delete(String name) {
		if (rooms.remove(name) == null)
			System.err.println("Error: Room " + name
					+ " does not exist. Room deletion failed.");
		else
			System.out.println("Room deleted.");
	}
	public static String getRoomList(){
		
		ArrayList<String> list = new ArrayList<String>();
		
        rooms.forEach((k,v) -> list.add(k));
		
		
		return list.toString() + "\n";
		
	}

	
	
}
