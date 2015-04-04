package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import peer.PeerID;
import chunk.ChunkID;

public class Database implements Serializable {

	private static final long serialVersionUID = 1L;

	// database of chunks being backed up by this peer
	private volatile HashMap<ChunkID, ArrayList<PeerID>> chunkDB;

	public Database() {
		chunkDB = new HashMap<ChunkID, ArrayList<PeerID>>();
	}

	public synchronized boolean hasChunk(ChunkID chunkID) {
		return chunkDB.containsKey(chunkID);
	}

	public synchronized void addChunk(ChunkID chunkID) {
		if (!hasChunk(chunkID))
			chunkDB.put(chunkID, new ArrayList<PeerID>());
	}

	public synchronized void addChunkMirror(ChunkID chunkID, PeerID peerID) {
		if (hasChunk(chunkID))
			if (!chunkDB.get(chunkID).contains(peerID))
				chunkDB.get(chunkID).add(peerID);
	}

	public synchronized void removeChunk(ChunkID chunkID) {
		chunkDB.remove(chunkID);
	}

	public synchronized int getChunkMirrorsSize(ChunkID chunkID) {
		return chunkDB.get(chunkID).size();
	}
	
	public synchronized HashMap<ChunkID, ArrayList<PeerID>> getDB() {
		return chunkDB;
	}

	@Override
	public String toString() {
		return chunkDB.toString();
	}

}
