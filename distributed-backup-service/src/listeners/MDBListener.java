package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

import peer.Handler;

public class MDBListener extends SocketListener {

	public MDBListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		new Thread(new Handler(packet, null)).start();
	}

}
