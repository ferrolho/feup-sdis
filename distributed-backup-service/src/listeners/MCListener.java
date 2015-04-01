package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

import peer.Handler;

public class MCListener extends SocketListener {

	public MCListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MC LISTENER HANDLER");

		// String msg = new String(packet.getData(), 0, packet.getLength());
		// System.out.println("MC: " + msg);

		new Thread(new Handler(packet)).start();
	}

}
