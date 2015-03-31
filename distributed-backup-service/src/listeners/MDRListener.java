package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class MDRListener extends SocketListener {

	public MDRListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	protected void handler(DatagramPacket packet) {
		System.out.println("MDR LISTENER HANDLER");
	}

}
