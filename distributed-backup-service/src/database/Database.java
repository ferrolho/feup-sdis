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
		chunkDB = new HashMap<ChunkID, ArrayList<PeerID>>();
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
	private volatile HashMap<ChunkID, ArrayList<PeerID>> chunkDB;

	public synchronized boolean hasChunk(ChunkID chunkID) {
		return chunkDB.containsKey(chunkID);
	}

	public synchronized void addChunk(ChunkID chunkID) {
		if (!hasChunk(chunkID)) {
			chunkDB.put(chunkID, new ArrayList<PeerID>());

			Peer.saveChunkDB();
		}
	}

	public synchronized void addChunkMirror(ChunkID chunkID, PeerID peerID) {
		if (hasChunk(chunkID)) {
			if (!chunkDB.get(chunkID).contains(peerID)) {
				chunkDB.get(chunkID).add(peerID);

				Peer.saveChunkDB();
			}
		}
	}

	public synchronized void removeChunk(ChunkID chunkID) {
		chunkDB.remove(chunkID);

		Peer.saveChunkDB();
	}

	public synchronized int getChunkMirrorsSize(ChunkID chunkID) {
		return chunkDB.get(chunkID).size();
	}

	public synchronized ArrayList<ChunkID> getChunkIDsOfFile(String fileID) {
		ArrayList<ChunkID> chunkIDs = new ArrayList<ChunkID>();

		for (ChunkID chunkID : chunkDB.keySet())
			if (chunkID.getFileID().equals(fileID))
				chunkIDs.add(chunkID);

		return chunkIDs;
	}

	/*
	 * Database of the files this peer requested the network to backup, and
	 * therefore can be restored.
	 */
	private volatile HashMap<String, FileInfo> restorableFiles;

	public synchronized void addRestorableFile(String fileName,
			FileInfo fileInfo) {
		restorableFiles.put(fileName, fileInfo);

		Peer.saveChunkDB();

		Log.info("Added restorable file:\n\t" + fileName + " - " + fileInfo);
	}

	public synchronized void removeRestorableFile(String fileName) {
		restorableFiles.remove(fileName);

		Peer.saveChunkDB();

		Log.info("Removed restorable file: " + fileName);
	}

	public synchronized boolean fileHasBeenBackedUp(String fileName) {
		return restorableFiles.containsKey(fileName);
	}

	public synchronized FileInfo getFileInfo(String fileName) {
		return restorableFiles.get(fileName);
	}

}
