package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import peer.Handler;
import peer.PeerID;
import service.ChunkID;

public class MCListener extends SocketListener {

	private volatile HashMap<ChunkID, ArrayList<PeerID>> storedConfirms;

	public MCListener(InetAddress address, int port) {
		super(address, port);

		storedConfirms = new HashMap<ChunkID, ArrayList<PeerID>>();
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MC HANDLR");
		new Thread(new Handler(packet)).start();
	}

	public synchronized void startSavingStoredConfirmsFor(ChunkID chunkID) {
		storedConfirms.put(chunkID, new ArrayList<PeerID>());

		System.out.println(storedConfirms.toString());
	}

	public synchronized void clearSavedStoredConfirmsFor(ChunkID chunkID) {
		storedConfirms.get(chunkID).clear();
	}

	public synchronized int getNumStoredConfirmsFor(ChunkID chunkID) {
		return storedConfirms.get(chunkID).size();
	}

	public synchronized void stopSavingStoredConfirmsFor(ChunkID chunkID) {
		storedConfirms.remove(chunkID);
	}

	public synchronized void processStoredConfirm(ChunkID chunkID,
			PeerID senderID) {
		if (storedConfirms.containsKey(chunkID))
			if (!storedConfirms.get(chunkID).contains(senderID))
				storedConfirms.get(chunkID).add(senderID);
	}

}
