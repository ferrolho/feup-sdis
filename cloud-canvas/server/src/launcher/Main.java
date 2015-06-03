package launcher;

import handler.Handler;

import java.io.IOException;

import server.HttpRequest;
import server.Server;
import utils.Utils;

public class Main {

	public static Server server;
	public static HttpRequest request;

	public static void main(String[] args) throws IOException {
		
		server = new Server(8000, "/canvas", new Handler());

		Server.init();

		Server.createRoom("sala1", Utils.getIPv4());
		Server.createRoom("sala2", Utils.getIPv4());
		Server.createRoom("sala3", Utils.getIPv4());

		System.out.println(Server.getRoomList());

		request = new HttpRequest("http://stackoverflow.com/search");
		System.out.println(request.GET(Utils.UTF_8));

	}

}
