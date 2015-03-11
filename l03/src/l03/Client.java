package l03;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

	private static String hostName;
	private static int port;
	private static RequestType oper;
	private static String plate, owner;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// build message
		String request = oper.toString();

		switch (oper) {
		case LOOKUP:
			request += Utils.SEPARATOR + plate;
			break;

		case REGISTER:
			request += Utils.SEPARATOR + plate + Utils.SEPARATOR + owner;
			break;
		}

		// open socket
		System.out.println("Opening socket...");
		System.out.println("----------------------------");
		DatagramSocket socket = new DatagramSocket();

		// send request
		byte[] buf = request.getBytes();
		InetAddress address = InetAddress.getByName(hostName);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
				port);
		socket.send(packet);
		System.out.print(request + " :: ");

		// receive response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());
		System.out.println(response);

		// close socket
		System.out.println("----------------------------");
		System.out.println("Closing socket...");
		socket.close();

		System.out.println("Client terminated.");
		System.out.println("----------------------------");
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Client <hostname> <port> <oper> <opnd>*");

			return false;
		} else {
			hostName = args[0];
			System.out.println("----------------------------");
			System.out.println("Host name: " + hostName);

			port = Integer.parseInt(args[1]);
			System.out.println("Port: " + port);

			String operStr = args[2];
			if (RequestType.REGISTER.toString().equals(operStr)) {
				if (args.length != 5) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <hostname> <port> register <plate number> <owner name>");

					return false;
				}

				oper = RequestType.REGISTER;
				plate = args[3];
				owner = args[4];

				System.out.println("Register " + plate + " " + owner);
			} else if (RequestType.LOOKUP.toString().equals(operStr)) {
				if (args.length != 4) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <hostname> <port> lookup <plate number>");

					return false;
				}

				oper = RequestType.LOOKUP;
				plate = args[3];

				System.out.println("Look up: " + plate);
			} else {
				System.out.println("Usage:");
				System.out
						.println("\tjava Client <hostname> <port> <oper> <opnd>*");
				System.out.println("\t<oper> - register | lookup");

				return false;
			}
		}

		return true;
	}

}
