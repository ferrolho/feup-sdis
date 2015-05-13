package server;

import java.net.InetAddress;
import java.util.HashMap;

public class Main {

	// map<roomName, room>
	private static HashMap<String, Room> rooms;

	public static void main(String[] args) {
		rooms = new HashMap<String, Room>();
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

}
