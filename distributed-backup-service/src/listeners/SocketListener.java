package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import peer.Peer;
import peer.PeerID;

public abstract class SocketListener implements Runnable {

	public static final int PACKET_MAX_SIZE = 65000;

	public MulticastSocket socket;

	public InetAddress address;
	public int port;

	public SocketListener(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public void run() {
		openSocket();

		byte[] buf = new byte[PACKET_MAX_SIZE];

		boolean done = false;
		while (!done) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				socket.receive(packet);

				PeerID senderID = new PeerID(packet.getAddress(),
						packet.getPort());

				// ignore packets sent by self
				if (!senderID.equals(Peer.getId()))
					handler(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		closeSocket();
	}

	private void openSocket() {
		try {
			socket = new MulticastSocket(port);

			socket.setTimeToLive(1);

			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract void handler(DatagramPacket packet) throws IOException;

	private void closeSocket() {
		if (socket != null)
			socket.close();
	}

}
