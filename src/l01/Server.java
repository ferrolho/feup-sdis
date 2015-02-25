package l01;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

	private static int port;

	public static void main(String[] args) throws IOException {
		port = Integer.parseInt(args[0]);
		System.out.println("Starting server on port " + port);

		System.out.println("Opening socket");
		DatagramSocket socket = new DatagramSocket(port);

		// receive packet
		System.out.println("Waiting for packet");

		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		System.out.println("Packet received");

		// send response
		System.out.println("Sending response packet");

		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);

		System.out.println("Packet sent");

		System.out.println("Closing socket");
		socket.close();

		System.out.println("Server terminated");
	}

}
