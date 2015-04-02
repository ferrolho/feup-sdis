package peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import listeners.MCListener;
import service.Chunk;
import service.Utils;

public class BackupInitiator implements Runnable {

	private static final int MAX_ATTEMPTS = 5;

	private File file;
	private int replicationDegree;

	private MCListener mcListener;

	public BackupInitiator(File file, int replicationDegree,
			MCListener mcListener) {
		this.file = file;
		this.replicationDegree = replicationDegree;

		this.mcListener = mcListener;
	}

	@Override
	public void run() {
		Chunk chunk = null;

		try {
			chunk = new Chunk(Utils.getFileID(file), 0, replicationDegree,
					Utils.getFileData(file));

			// TODO improve this method to split files
		} catch (FileNotFoundException e) {
			Utils.printError("file not found");
			return;
		}

		ArrayList<PeerID> confirmedPeers = new ArrayList<PeerID>();
		mcListener.confirmedPeers.put(chunk.getFileID(), confirmedPeers);

		int attempt = 0;
		long waitingTime = 500;

		boolean done = false;
		while (!done) {
			confirmedPeers.clear();

			Peer.synchedHandler.putChunk(chunk);

			try {
				System.out.println("Waiting for STOREDs for " + waitingTime
						+ "ms");
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(confirmedPeers.size()
					+ " peers have stored the chunk");
			System.out.println();

			if (confirmedPeers.size() < replicationDegree) {
				attempt++;

				if (attempt > MAX_ATTEMPTS)
					done = true;
				else
					waitingTime *= 2;
			} else
				done = true;
		}
	}
}
