package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {

	private static int port;
	private static HashMap<String, String> plates;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// create database
		plates = new HashMap<String, String>();

		// open socket
		System.out.println("Opening socket...");
		System.out.println("----------------------------");
		DatagramSocket socket = new DatagramSocket(port);

		boolean done = false;
		while (!done) {
			// receive request
			System.out.println("WAITING FOR REQUEST...");
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String request = new String(packet.getData(), 0, packet.getLength());
			System.out.println("RECEIVED: " + request);

			// process request
			String[] tokens = request.split(Utils.SEPARATOR);
			RequestType oper = RequestType.REGISTER.toString()
					.equals(tokens[0]) ? RequestType.REGISTER
					: RequestType.LOOKUP;
			String plate = tokens[1], owner;

			String response = "-response string not defined-";
			switch (oper) {
			case LOOKUP:
				if (plates.containsKey(plate)) {
					owner = plates.get(plate);
					response = owner;
				} else {
					response = "NOT_FOUND";
				}

				break;

			case REGISTER:
				owner = tokens[2];

				if (plates.containsKey(plate)) {
					response = "-1";
				} else {
					plates.put(plate, owner);
					response = Integer.toString(plates.size());
				}

				break;

			default:
				break;
			}

			// send response
			System.out.println("SENT: " + response);
			buf = response.getBytes();
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
		}

		// close socket
		System.out.println("----------------------------");
		System.out.println("Closing socket...");
		socket.close();

		System.out.println("Server terminated.");
		System.out.println("----------------------------");
	}

	private static boolean validArgs(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage:");
			System.out.println("\tjava Server <port>");

			return false;
		} else {
			System.out.println("----------------------------");
			port = Integer.parseInt(args[0]);
			System.out.println("Port: " + port);

			return true;
		}
	}

}
