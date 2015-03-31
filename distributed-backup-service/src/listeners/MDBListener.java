package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

import peer.TestHandler;

public class MDBListener extends SocketListener {

	TestHandler testHandler;

	public MDBListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MDB LISTENER HANDLER");

		// new Handler(packet).start();
		testHandler = new TestHandler(packet);
		testHandler.start();
	}

}
