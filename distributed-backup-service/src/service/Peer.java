package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Peer {

	private static InetAddress mcAddress;
	private static int mcPort;

	private static InetAddress mdbAddress;
	private static int mdbPort;

	private static InetAddress mdrAddress;
	private static int mdrPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// multicast control channel
		MulticastSocket mcSocket = new MulticastSocket();
		mcSocket.setTimeToLive(1);

		// multicast data backup channel
		MulticastSocket mdbSocket = new MulticastSocket();
		mdbSocket.setTimeToLive(1);

		// multicast data restore channel
		MulticastSocket mdrSocket = new MulticastSocket();
		mdrSocket.setTimeToLive(1);

		String test;
		DatagramPacket packet;

		test = "mc test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mcAddress, mcPort);
		mcSocket.send(packet);

		test = "mdb test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mdbAddress, mdbPort);
		mdbSocket.send(packet);

		test = "mdr test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mdrAddress, mdrPort);
		mdrSocket.send(packet);

		// repl was here

		mcSocket.close();
		mdbSocket.close();
		mdrSocket.close();
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		if (args.length != 0 && args.length != 6) {
			System.out.println("Usage:");
			System.out.println("\tjava Server");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");

			return false;
		} else if (args.length == 0) {
			mcAddress = InetAddress.getByName("224.0.0.0");
			mcPort = 8000;

			mdbAddress = InetAddress.getByName("224.0.0.0");
			mdbPort = 8001;

			mdrAddress = InetAddress.getByName("224.0.0.0");
			mdrPort = 8002;

			return true;
		} else {
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);

			return true;
		}
	}

}
