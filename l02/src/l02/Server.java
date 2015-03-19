package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Server {

	private static String serviceAddressStr;
	private static int servicePort;

	private static String multicastAddressStr;
	private static int multicastPort;

	private static HashMap<String, String> plates;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// create database
		plates = new HashMap<String, String>();

		// open multicast socket
		MulticastSocket multicastSocket = new MulticastSocket();
		multicastSocket.setTimeToLive(1);

		InetAddress multicastAddress = InetAddress
				.getByName(multicastAddressStr);

		// open server socket
		DatagramSocket serverSocket = new DatagramSocket(servicePort);
		serverSocket.setSoTimeout(1000);

		// 1s interval advertisement control variables
		long elapsedTime = 1000;
		long prevTime = System.currentTimeMillis();

		boolean done = false;
		while (!done) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				System.out.print("WAITING FOR REQUEST... ");
				serverSocket.receive(packet);
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
						response = "ERROR";
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
				serverSocket.send(packet);
			} catch (SocketTimeoutException e) {
				System.out.println(e);
			}

			// BEGIN --- service advertisement every 1 second
			long currentTime = System.currentTimeMillis();

			elapsedTime += currentTime - prevTime;
			prevTime = currentTime;

			if (elapsedTime >= 1000) {
				elapsedTime -= 1000;

				String advertisement = serviceAddressStr + ":"
						+ Integer.toString(servicePort);
				packet = new DatagramPacket(advertisement.getBytes(),
						advertisement.getBytes().length, multicastAddress,
						multicastPort);
				multicastSocket.send(packet);

				System.out.println("multicast: " + multicastAddressStr + " "
						+ multicastPort + ": " + serviceAddressStr + " "
						+ servicePort);
			}
			// END ---service advertisement
		}

		// close server socket
		serverSocket.close();

		// close multicast socket
		multicastSocket.close();
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		if (args.length != 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Server <servicePort> <multicastIP> <multicastPort>");

			return false;
		} else {
			serviceAddressStr = Utils.getIPv4();
			servicePort = Integer.parseInt(args[0]);

			multicastAddressStr = args[1];
			multicastPort = Integer.parseInt(args[2]);

			return true;
		}
	}

}
