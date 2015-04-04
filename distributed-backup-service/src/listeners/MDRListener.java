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

	}

	@Override
	protected void handler(DatagramPacket packet) {
		System.out.println("MDR LISTENER HANDLER");
		new Thread(new Handler(packet)).start();
	}

	/*
	 * Container to know if it is necessary to send a chunk to the network, or
	 * if someone has already sent it.
	 */
	private volatile HashMap<ChunkID, Boolean> sentCHUNKs;

	public synchronized void startSavingCHUNKsFor(ChunkID chunkID) {
		sentCHUNKs.put(chunkID, false);

		System.out.println(sentCHUNKs.toString());
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

	public synchronized void feedChunk(Chunk chunk) {
		chunks.get(chunk.getID().getFileID()).add(chunk);

		notifyAll();
	}

	public synchronized Chunk consumeChunk(String fileID) {
		ArrayList<Chunk> fileChunks = chunks.get(fileID);

		return fileChunks.isEmpty() ? null : fileChunks.remove(0);
	}

	public synchronized void stopSavingFileChunks(String fileID) {
		chunks.remove(fileID);
	}

}
