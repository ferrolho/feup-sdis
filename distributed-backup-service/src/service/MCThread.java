package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MCThread extends Thread {

	public MulticastSocket socket;

	private InetAddress address;
	private int port;

	public MCThread(InetAddress mcAddress, int mcPort) {
		this.address = mcAddress;
		this.port = mcPort;
	}

	public void run() {
		try {
			// multicast control channel
			socket = new MulticastSocket(port);

			socket.setLoopbackMode(true);
			socket.setTimeToLive(1);

			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] buf = new byte[64000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		boolean done = false;
		while (!done) {
			try {
				socket.receive(packet);
				String msg = new String(packet.getData(), 0, packet.getLength());

				System.out.println("MC: " + msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (socket != null)
			socket.close();
	}

}
