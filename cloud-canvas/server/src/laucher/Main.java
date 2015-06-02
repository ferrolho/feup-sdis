package laucher;

import handler.Handler;

import java.io.IOException;
import java.net.InetAddress;

import server.Server;

public class Main {
	public static Server server;

	public static void main(String[] args) throws IOException {

		server = new Server(8000, "/canvas", new Handler());

		Server.init();

		Server.createRoom("sala 1", InetAddress.getLocalHost());
		Server.createRoom("sala 2", InetAddress.getLocalHost());
		Server.createRoom("sala 3", InetAddress.getLocalHost());

		System.out.println(Server.getRoomList());
	}
}
