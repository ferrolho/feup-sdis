package l01;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

	// TODO place this somewhere better
	private static final String SEPARATOR = "_";

	private static String hostName;
	private static int port;
	private static MessageType oper;
	private static String plate, owner;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		String message = oper.toString();

		switch (oper) {
		case LOOKUP:
			message += SEPARATOR + plate;
			break;

		case REGISTER:
			message += SEPARATOR + plate + SEPARATOR + owner;
			break;
		}

		DatagramSocket socket = new DatagramSocket();

		// send request
		byte[] buf = message.getBytes();
		InetAddress address = InetAddress.getByName(hostName);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
				port);
		socket.send(packet);

		// response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String received = new String(packet.getData(), 0, packet.getLength());
		System.out.println("Received: " + received);

		System.out.println("Closing socket");
		socket.close();

		System.out.println("Client terminated");
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Client <hostname> <port> <oper> <opnd>*");

			return false;
		} else {
			hostName = args[0];
			System.out.println("Host name: " + hostName);

			port = Integer.parseInt(args[1]);
			System.out.println("Starting client on port " + port);

			String operStr = args[2];
			if (MessageType.REGISTER.toString().equals(operStr)) {
				if (args.length != 5) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <hostname> <port> register <plate number> <owner name>");

					return false;
				}

				oper = MessageType.REGISTER;
				plate = args[3];
				owner = args[4];

				System.out.println("Registering plate " + plate + " of owner "
						+ owner);
			} else if (MessageType.LOOKUP.toString().equals(operStr)) {
				if (args.length != 4) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <hostname> <port> lookup <plate number>");

					return false;
				}

				oper = MessageType.LOOKUP;
				plate = args[3];

				System.out.println("Looking up plate " + plate);
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
