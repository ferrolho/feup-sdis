package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import peer.PeerID;
import chunk.ChunkID;
import service.Handler;

public class MDBListener extends SocketListener {

	private volatile HashMap<ChunkID, ArrayList<PeerID>> PUTCHUNKsLog;

	public MDBListener(InetAddress address, int port) {
		super(address, port);

		PUTCHUNKsLog = new HashMap<ChunkID, ArrayList<PeerID>>();
	}

	@Override
	public void handler(DatagramPacket packet) {
		new Thread(new Handler(packet)).start();
	}

	public synchronized void startSavingPUTCHUNKsFor(ChunkID chunkID) {
		PUTCHUNKsLog.put(chunkID, new ArrayList<PeerID>());
	}

	public synchronized int getNumPUTCHUNKsFor(ChunkID chunkID) {
		return PUTCHUNKsLog.get(chunkID).size();
	}

	public synchronized void stopSavingPUTCHUNKsFor(ChunkID chunkID) {
		PUTCHUNKsLog.remove(chunkID);
	}

	public synchronized void processPUTCHUNK(ChunkID chunkID, PeerID senderID) {
		if (PUTCHUNKsLog.containsKey(chunkID))
			if (!PUTCHUNKsLog.get(chunkID).contains(senderID))
				PUTCHUNKsLog.get(chunkID).add(senderID);
	}

}
