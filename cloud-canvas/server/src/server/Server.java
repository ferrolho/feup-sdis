package server;

import handler.Handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.net.httpserver.HttpServer;

import elements.Room;

/**
 * Class that represents the application server
 * 
 * @author canvas
 *
 */
public class Server {

	private static HashMap<String, Room> rooms;

	public static HttpServer server;

	/**
	 * Server class constructor
	 * 
	 * @param port
	 *            the port
	 * @param context
	 *            the context
	 * @param handler
	 *            the handler
	 * @throws IOException
	 *             if cannot create the http server
	 */
	public Server(int port, String context, Handler handler) throws IOException {
		rooms = new HashMap<String, Room>();

		// configuring server
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext(context, handler);

			// creates a default executor
			server.setExecutor(null);

		} catch (IOException e) {
			System.out.println("Exception caught: " + e.getMessage()
					+ " in Server.contructor");
		}
	}

	/**
	 * starts the server
	 */
	public static void init() {
		server.start();
	}

	/**
	 * Creates a room in the server
	 * 
	 * @param name
	 *            the room's name
	 * @param ip
	 *            the room's ip
	 */
	public static void createRoom(String name, InetAddress ip) {
		if (!rooms.containsKey(name))
			rooms.put(name, new Room(ip));
		else
			System.err
					.println("Room name already exists. Room creation failed.");
	
	
		incUsersRoom(name);
	}

	/**
	 * Increments the users number presents at the room
	 * 
	 * @param name
	 *            the room's name
	 * @return the number of users in the room
	 */
	public static int incUsersRoom(String name) {
		rooms.get(name).incNumUsers();
		return rooms.get(name).getNumUsers();
	}

	/**
	 * Decrements the users number presents at the room
	 * 
	 * @param name
	 *            the room's name
	 * @return the number of the users in the room or 0 if the room is empty
	 */
	public static int decUsersRoom(String name) {
		System.out.println("DBG: -" + name + "-");

		if (!rooms.containsKey(name))
			return -1;

		Room room = rooms.get(name);

		room.decNumUsers();

		if (room.isEmpty()) {
			deleteRoom(name);
			return 0;
		} else {
			return rooms.get(name).getNumUsers();
		}
	}

	/**
	 * Delete a room from the server
	 * 
	 * @param name
	 *            of the target room to delete
	 */
	private static void deleteRoom(String name) {
		if (rooms.remove(name) == null)
			System.err.println("Error: Room " + name
					+ " does not exist. Room deletion failed.");
		else
			System.out.println("Room deleted.");
	}

	/**
	 * Obtained all information of a room
	 * 
	 * @return a string with the information
	 */
	public static String getRoomList() {
		ArrayList<String> list = new ArrayList<String>();

		rooms.forEach((k, v) -> list.add(k + "," + v.getIp().getHostAddress()));

		String str = "";

		for (int i = 0; i < list.size(); i++) {
			str += list.get(i);

			if (i < list.size() - 1)
				str += " ";
		}

		return str;
	}

	public static boolean roomExists(String roomName) {
		return rooms.containsKey(roomName);
	}

}
