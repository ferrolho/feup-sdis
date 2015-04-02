package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import peer.Handler;
import peer.PeerID;
import service.ChunkID;

public class MCListener extends SocketListener {

	public volatile HashMap<ChunkID, ArrayList<PeerID>> confirmedPeers;

	public MCListener(InetAddress address, int port) {
		super(address, port);

		confirmedPeers = new HashMap<ChunkID, ArrayList<PeerID>>();
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MC HANDLR");
		new Thread(new Handler(packet, confirmedPeers)).start();
	}

}
