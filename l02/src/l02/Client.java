package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {

	private static String multicastIP, serviceIP;
	private static int multicastPort, servicePort;
	private static RequestType oper;
	private static String plate, owner;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// ////////////////////////
		// join a Multicast group and send the group salutations
		InetAddress group = InetAddress.getByName(multicastIP);
		MulticastSocket socket = new MulticastSocket(multicastPort);
		socket.joinGroup(group);

		byte[] buf = new byte[256];

		boolean done = false;
		while (!done) {
			// Receive the information and print it.
			DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
			socket.receive(msgPacket);

			String msg = new String(buf, 0, buf.length);
			System.out.println("Socket 1 received msg: " + msg);
		}

		// OK, I'm done talking - leave the group...
		socket.leaveGroup(group);
		socket.close();
		// ////////////////////////

		/*
		System.out.println("multicast: " + multicastIP + " " + multicastPort
				+ ": " + serviceIP + " " + servicePort);

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
		InetAddress address = InetAddress.getByName(multicastIP);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
				multicastPort);
		socket.send(packet);
		System.out.println("SENT: " + request);

		// receive response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());
		System.out.println("RECEIVED: " + response);

		// close socket
		System.out.println("----------------------------");
		System.out.println("Closing socket...");
		socket.close();

		System.out.println("Client terminated.");
		System.out.println("----------------------------");
		*/
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Client <multicastIP> <multicastPort> <oper> <opnd>*");

			return false;
		} else {
			System.out.println("----------------------------");

			multicastIP = args[0];
			System.out.println("Host name: " + multicastIP);

			multicastPort = Integer.parseInt(args[1]);
			System.out.println("Port: " + multicastPort);

			String operStr = args[2];
			if (RequestType.REGISTER.toString().equals(operStr)) {
				if (args.length != 5) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <multicastIP> <multicastPort> register <plate number> <owner name>");

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
							.println("\tjava Client <multicastIP> <multicastPort> lookup <plate number>");

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
