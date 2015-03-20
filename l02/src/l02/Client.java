package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {

	private static String serviceAddressStr;
	private static int servicePort;

	private static String multicastAddressStr;
	private static int multicastPort;

	private static RequestType oper;
	private static String plate, owner;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		InetAddress group = InetAddress.getByName(multicastAddressStr);
		MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
		multicastSocket.joinGroup(group);

		byte[] buf = new byte[256];
		DatagramPacket multicastPacket = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(multicastPacket);

		String msg = new String(multicastPacket.getData());
		String[] parts = msg.split(":");
		serviceAddressStr = parts[0];
		servicePort = Integer.parseInt(parts[1].replaceAll("[^\\d.]", ""));

		System.out.println("multicast: " + multicastAddressStr + " "
				+ multicastPort + ": " + serviceAddressStr + " " + servicePort);

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
		DatagramSocket socket = new DatagramSocket();

		// send request
		buf = request.getBytes();
		InetAddress address = InetAddress.getByName(serviceAddressStr);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
				servicePort);
		socket.send(packet);

		// receive response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());

		System.out.println(request + " :: " + response);

		// close socket
		socket.close();

		multicastSocket.leaveGroup(group);
		multicastSocket.close();
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Client <multicastIP> <multicastPort> <oper> <opnd>*");

			return false;
		} else {
			multicastAddressStr = args[0];
			multicastPort = Integer.parseInt(args[1]);

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
			} else if (RequestType.LOOKUP.toString().equals(operStr)) {
				if (args.length != 4) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <multicastIP> <multicastPort> lookup <plate number>");

					return false;
				}

				oper = RequestType.LOOKUP;
				plate = args[3];
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
