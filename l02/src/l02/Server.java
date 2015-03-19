package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class Server {

	private static int servicePort;

	private static String multicastIP;
	private static int multicastPort;

	private static HashMap<String, String> plates;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// create database
		plates = new HashMap<String, String>();

		// open socket
		System.out.println("Opening socket...");
		System.out.println("----------------------------");
		MulticastSocket socket = new MulticastSocket();
		socket.setSoTimeout(1000);
		socket.setTimeToLive(1);

		// join a multicast group and send the group salutations
		InetAddress address = InetAddress.getByName(multicastIP);

		long elapsedTime = 1000;
		long prevTime = System.currentTimeMillis();

		boolean done = false;
		while (!done) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				System.out.println("WAITING FOR REQUEST...");
				socket.receive(packet);
				String request = new String(packet.getData(), 0,
						packet.getLength());
				System.out.println("RECEIVED: " + request);

				// process request
				String[] tokens = request.split(Utils.SEPARATOR);
				RequestType oper = RequestType.REGISTER.toString().equals(
						tokens[0]) ? RequestType.REGISTER : RequestType.LOOKUP;
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
				InetAddress cliAddress = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, cliAddress, port);
				socket.send(packet);
			} catch (SocketTimeoutException e) {
				System.out.println("TIMEOUT REACHED! " + e);
			}

			// --- service advertisement every 1 second
			long currentTime = System.currentTimeMillis();

			elapsedTime += currentTime - prevTime;
			prevTime = currentTime;

			if (elapsedTime >= 1000) {
				elapsedTime -= 1000;

				String msg = "localhost " + servicePort;
				System.out.println(msg);

				packet = new DatagramPacket(msg.getBytes(),
						msg.getBytes().length, address, multicastPort);
				socket.send(packet);

				System.out.println("Server sent packet with msg: " + msg);
			}
		}

		// close socket
		System.out.println("----------------------------");
		System.out.println("Closing socket...");
		socket.close();

		System.out.println("Server terminated.");
		System.out.println("----------------------------");
	}

	private static boolean validArgs(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Server <servicePort> <multicastIP> <multicastPort>");

			return false;
		} else {
			System.out.println("----------------------------");

			servicePort = Integer.parseInt(args[0]);
			System.out.println("Server port: " + servicePort);

			multicastIP = args[1];
			System.out.println("Multicast IP: " + multicastIP);

			multicastPort = Integer.parseInt(args[2]);
			System.out.println("Multicast port: " + multicastPort);

			return true;
		}
	}

}
