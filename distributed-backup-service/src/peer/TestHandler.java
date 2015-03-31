package peer;

import java.net.DatagramPacket;

public class TestHandler extends Thread {

	private DatagramPacket packet;

	public TestHandler(DatagramPacket packet) {
		this.packet = packet;
	}

	public void run() {
		System.out.println("TEST: " + packet.getLength());
	}

}
