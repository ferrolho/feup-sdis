package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.net.httpserver.HttpServer;

public class Server {
	// map<roomName, room>
	private static HashMap<String, Room> rooms;
	
	public static HttpServer server;
	
	public Server(int port) throws IOException {
		rooms = new HashMap<String, Room>();
		try {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/canvas", new RequestHandler());
		server.setExecutor(null); // creates a default executor
		} catch(IOException e) {
			System.out.println("Exception caught: " + e.getMessage() + " in Server.contructor");
		}
	}
	
	public static void Init() {
		server.start();
	}
	
	public static void CreateRoom(String name, InetAddress ip) {
		if (!rooms.containsKey(name))
			rooms.put(name, new Room(ip));
		else
			System.err
					.println("Room name already exists. Room creation failed.");
	}
	
	public static int incUsers(String name) {
		rooms.get(name).incNumUsers();
		return rooms.get(name).getNumUsers();
	}

	public static int decUsers(String name) {
		Room room = rooms.get(name);
 
		room.decNumUsers();

		if (room.isEmpty()){
			delete(name);
			return 0;
		}
		else{
			
			return rooms.get(name).getNumUsers();
		}
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
		
        rooms.forEach((k,v) -> list.add(k + "," + v.getIp().toString()));
		
		
		return list.toString() + "\n";
		
	}

}
