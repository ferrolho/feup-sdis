package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class MCListener extends SocketListener {

	public MCListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		String msg = new String(packet.getData(), 0, packet.getLength());

		System.out.println("MC: " + msg);
	}

}
