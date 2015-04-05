package listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import service.Handler;
import chunk.Chunk;
import chunk.ChunkID;

public class MDRListener extends SocketListener {

	public MDRListener(InetAddress address, int port) {
		super(address, port);

		sentCHUNKs = new HashMap<ChunkID, Boolean>();
		chunks = new HashMap<String, ArrayList<Chunk>>();
	}

	@Override
	protected void handler(DatagramPacket packet) {
		new Thread(new Handler(packet)).start();
	}

	/*
	 * Container to know if it is necessary to send a chunk to the network, or
	 * if someone has already sent it.
	 */
	private volatile HashMap<ChunkID, Boolean> sentCHUNKs;

	public synchronized void startSavingCHUNKsFor(ChunkID chunkID) {
		sentCHUNKs.put(chunkID, false);
	}

	public synchronized void registerCHUNK(ChunkID chunkID) {
		if (sentCHUNKs.containsKey(chunkID))
			sentCHUNKs.put(chunkID, true);
	}

	public synchronized boolean stopSavingCHUNKsFor(ChunkID chunkID) {
		return sentCHUNKs.remove(chunkID);
	}

	/*
	 * Container to feed with chunks this peer asked for. The restore initiator
	 * will consume these.
	 */
	private volatile HashMap<String, ArrayList<Chunk>> chunks;

	public synchronized void prepareToReceiveFileChunks(String fileID) {
		chunks.put(fileID, new ArrayList<Chunk>());
	}

	public synchronized boolean feedingChunksOfFile(String fileID) {
		return chunks.containsKey(fileID);
	}

	public synchronized void feedChunk(Chunk chunk) {
		chunks.get(chunk.getID().getFileID()).add(chunk);

		notifyAll();
	}

	public synchronized Chunk consumeChunk(String fileID) {
		ArrayList<Chunk> fileChunks = chunks.get(fileID);

		Chunk chunk = fileChunks.isEmpty() ? null : chunks.get(fileID)
				.remove(0);

		while (chunk == null) {
			try {
				wait();
			} catch (InterruptedException e) {
			}

			chunk = fileChunks.isEmpty() ? null : chunks.get(fileID).remove(0);
		}

		return chunk;
	}

	public synchronized void stopSavingFileChunks(String fileID) {
		chunks.remove(fileID);
	}

}
