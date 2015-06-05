package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class Utils {

	public static Random random = new Random();

	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=";
	public static final String UTF_8 = "UTF-8";

	public static byte[] concatBytes(byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;

		byte[] c = new byte[aLen + bLen];

		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static InetAddress getIPv4() throws IOException {
		MulticastSocket socket = new MulticastSocket();
		socket.setTimeToLive(0);

		InetAddress addr = InetAddress.getByName("225.0.0.0");
		socket.joinGroup(addr);

		byte[] bytes = new byte[0];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr,
				socket.getLocalPort());

		socket.send(packet);
		socket.receive(packet);

		socket.close();

		return packet.getAddress();
	}
	
	public static void log(String message) {
		System.out.println("LOG: " + message);
	}

}
