package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MCThread extends Thread {

	public MulticastSocket mcSocket;
	private InetAddress mcAddress;
	private int mcPort;

	public MCThread(InetAddress mcAddress, int mcPort) {
		this.mcAddress = mcAddress;
		this.mcPort = mcPort;
	}

	public void run() {
		try {
			// multicast control channel
			mcSocket = new MulticastSocket(mcPort);

			mcSocket.setLoopbackMode(true);
			// mcSocket.setSoTimeout(1000);
			mcSocket.setTimeToLive(1);

			mcSocket.joinGroup(mcAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] buf = new byte[64000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		try {
			mcSocket.receive(packet);
			String msg = new String(packet.getData(), 0, packet.getLength());

			System.out.println("MC: " + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
