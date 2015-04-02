package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import peer.Handler;
import peer.PeerID;

public class MCListener extends SocketListener {

	public HashMap<String, ArrayList<PeerID>> confirmedPeers;

	public MCListener(InetAddress address, int port) {
		super(address, port);

		confirmedPeers = new HashMap<String, ArrayList<PeerID>>();
	}

	@Override
	public void handler(DatagramPacket packet) {
		new Thread(new Handler(packet, confirmedPeers)).start();
	}
}
