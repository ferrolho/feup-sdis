package peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import service.Chunk;
import service.Utils;
import utils.Log;

public class BackupInitiator implements Runnable {

	private static final long INITIAL_WAITING_TIME = 500;
	private static final int MAX_ATTEMPTS = 5;

	private File file;
	private int replicationDegree;

	public BackupInitiator(File file, int replicationDegree) {
		this.file = file;
		this.replicationDegree = replicationDegree;
	}

	@Override
	public void run() {
		Chunk chunk = null;

		try {
			chunk = new Chunk(Utils.getFileID(file), 0, replicationDegree,
					Utils.getFileData(file));

			// TODO improve this method to split files
		} catch (FileNotFoundException e) {
			Log.error("file not found");
			return;
		}

		ArrayList<PeerID> receivedSTOREDs = new ArrayList<PeerID>();

		Peer.getMcListener().confirmedPeers.put(chunk.getChunkID(),
				receivedSTOREDs);
		System.out.println(Peer.getMcListener().confirmedPeers.toString());

		long waitingTime = INITIAL_WAITING_TIME;
		int attempt = 0;

		boolean done = false;
		while (!done) {
			receivedSTOREDs.clear();

			Peer.synchedHandler.sendPUTCHUNK(chunk);

			try {
				System.out.println("Waiting for STOREDs for " + waitingTime
						+ "ms");
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(receivedSTOREDs.size() + " of "
					+ replicationDegree + " peers stored the chunk");
			System.out.println();

			if (receivedSTOREDs.size() < replicationDegree) {
				attempt++;

				if (attempt > MAX_ATTEMPTS)
					done = true;
				else
					waitingTime *= 2;
			} else
				done = true;
		}

		Peer.getMcListener().confirmedPeers.remove(chunk.getChunkID()
				.getFileID());
	}
}
