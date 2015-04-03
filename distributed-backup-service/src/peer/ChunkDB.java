package peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import chunk.ChunkID;

public class ChunkDB implements Serializable {

	private static final long serialVersionUID = 1L;

	private volatile HashMap<ChunkID, ArrayList<PeerID>> db;

	public ChunkDB() {
		db = new HashMap<ChunkID, ArrayList<PeerID>>();
	}

	public synchronized boolean hasChunk(ChunkID chunkID) {
		return db.containsKey(chunkID);
	}

	public synchronized void addChunk(ChunkID chunkID) {
		if (!hasChunk(chunkID))
			db.put(chunkID, new ArrayList<PeerID>());
	}

	public synchronized void addChunkMirror(ChunkID chunkID, PeerID peerID) {
		if (hasChunk(chunkID))
			if (!db.get(chunkID).contains(peerID))
				db.get(chunkID).add(peerID);
	}

	public synchronized void removeChunk(ChunkID chunkID) {
		db.remove(chunkID);
	}

	public synchronized int getChunkMirrorsSize(ChunkID chunkID) {
		return db.get(chunkID).size();
	}

	@Override
	public String toString() {
		return db.toString();
	}

}
