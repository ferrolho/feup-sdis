package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;

import service.Handler;

public class MDBListener extends SocketListener {

	public MDBListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MDB HANDLR");
		new Thread(new Handler(packet)).start();
	}

}
