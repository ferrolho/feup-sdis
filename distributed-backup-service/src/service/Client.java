package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Client {

	private static InetAddress mcAddress;
	private static int mcPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		MulticastSocket multicastSocket = new MulticastSocket(mcPort);
		multicastSocket.joinGroup(mcAddress);

		byte[] buf = new byte[256];
		DatagramPacket multicastPacket = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(multicastPacket);

		String msg = new String(multicastPacket.getData());

		System.out.println("mgs: " + msg);

		multicastSocket.leaveGroup(mcAddress);
		multicastSocket.close();
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		mcAddress = InetAddress.getByName(args[0]);
		mcPort = Integer.parseInt(args[1]);

		return true;
	}

}
