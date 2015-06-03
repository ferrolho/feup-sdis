package launcher;

import handler.Handler;

import java.io.IOException;

import server.Server;

public class Main {

	public static Server server;

	public static void main(String[] args) throws IOException {

		server = new Server(8000, "/canvas", new Handler());

		Server.init();

		Server.createRoom("sala1", Server.getIPv4());
		Server.createRoom("sala2", Server.getIPv4());
		Server.createRoom("sala3", Server.getIPv4());

		System.out.println(Server.getRoomList());
	}

}
