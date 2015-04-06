package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import peer.Peer;
import peer.PeerID;
import utils.Log;
import chunk.ChunkID;

public class Database implements Serializable {

	private static final long serialVersionUID = 1L;

	public Database() {
		chunkDB = new HashMap<ChunkID, ChunkInfo>();
		restorableFiles = new HashMap<String, FileInfo>();
	}

	@Override
	public String toString() {
		String db;

		db = "Chunk database:\n";
		db += chunkDB.toString() + "\n";
		db += "\n";
		db += "Restorable files:\n";
		db += restorableFiles.toString() + "\n";

		return db;
	}

	/*
	 * Database of chunks being backed up by this peer
	 */
	private volatile HashMap<ChunkID, ChunkInfo> chunkDB;

	public synchronized boolean hasChunk(ChunkID chunkID) {
		return chunkDB.containsKey(chunkID);
	}

	public synchronized void addChunk(ChunkID chunkID, int replicationDegree) {
		if (!hasChunk(chunkID)) {
			chunkDB.put(chunkID, new ChunkInfo(replicationDegree,
					new ArrayList<PeerID>()));

			Peer.saveDatabase();
		}
	}

	public synchronized void removeChunk(ChunkID chunkID) {
		chunkDB.remove(chunkID);

		Peer.saveDatabase();
	}

	public synchronized void addChunkMirror(ChunkID chunkID, PeerID peerID) {
		if (hasChunk(chunkID)) {
			if (!chunkDB.get(chunkID).getMirrors().contains(peerID)) {
				chunkDB.get(chunkID).getMirrors().add(peerID);

				Peer.saveDatabase();
			}
		}
	}

	public synchronized void removeChunkMirror(ChunkID chunkID, PeerID peerID) {
		chunkDB.get(chunkID).removeMirror(peerID);
	}

	public synchronized int getChunkReplicationDegree(ChunkID chunkID) {
		return chunkDB.get(chunkID).getReplicationDegree();
	}

	public synchronized int getChunkMirrorsSize(ChunkID chunkID) {
		return chunkDB.get(chunkID).getMirrors().size();
	}

	public synchronized ArrayList<ChunkID> getChunkIDsOfFile(String fileID) {
		ArrayList<ChunkID> chunkIDs = new ArrayList<ChunkID>();

		for (ChunkID chunkID : chunkDB.keySet())
			if (chunkID.getFileID().equals(fileID))
				chunkIDs.add(chunkID);

		return chunkIDs;
	}

	public synchronized ChunkID getMostBackedUpChunk() {
		ChunkID best = null;

		for (ChunkID chunkID : chunkDB.keySet()) {
			if (best == null
					|| getChunkMirrorsSize(chunkID) > getChunkMirrorsSize(best)) {
				best = chunkID;
			}
		}

		return best;
	}

	/*
	 * Database of the files this peer requested the network to backup, and
	 * therefore can be restored.
	 */
	private volatile HashMap<String, FileInfo> restorableFiles;

	public synchronized void addRestorableFile(String fileName,
			FileInfo fileInfo) {
		restorableFiles.put(fileName, fileInfo);

		Peer.saveDatabase();

		Log.info("Added restorable file:\n\t" + fileName + " - " + fileInfo);
	}

	public synchronized void removeRestorableFile(String fileName) {
		restorableFiles.remove(fileName);

		Peer.saveDatabase();

		Log.info("Removed restorable file: " + fileName);
	}

	public synchronized boolean fileHasBeenBackedUp(String fileName) {
		return restorableFiles.containsKey(fileName);
	}

	public synchronized FileInfo getFileInfo(String fileName) {
		return restorableFiles.get(fileName);
	}

	public synchronized boolean canSaveChunksOf(String fileID) {
		for (FileInfo fileInfo : restorableFiles.values()) {
			if (fileInfo.getFileID().equals(fileID))
				return false;
		}

		return true;
	}

}
