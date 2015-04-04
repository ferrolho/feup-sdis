package initiators;

import java.io.File;
import java.io.FileNotFoundException;

import peer.Peer;
import utils.FileManager;
import utils.Log;
import utils.Utils;
import chunk.Chunk;

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
		if (Peer.getChunkDB().fileHasBeenBackedUp(file.getName())) {
			Log.error("A file with this name has alread been backed up.");
			return;
		}

		Chunk chunk = null;

		try {
			chunk = new Chunk(Utils.getFileID(file), 0, replicationDegree,
					FileManager.loadFile(file));

			// TODO improve this method to split files
		} catch (FileNotFoundException e) {
			Log.error("file not found");
			return;
		}

		Peer.getMcListener().startSavingStoredConfirmsFor(chunk.getID());

		long waitingTime = INITIAL_WAITING_TIME;
		int attempt = 0;

		boolean done = false;
		while (!done) {
			Peer.getMcListener().clearSavedStoredConfirmsFor(chunk.getID());

			Peer.commandForwarder.sendPUTCHUNK(chunk);

			try {
				System.out.println("Waiting for STOREDs for " + waitingTime
						+ "ms");
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(Peer.getMcListener().getNumStoredConfirmsFor(
					chunk.getID())
					+ " of " + replicationDegree + " peers stored the chunk");
			System.out.println();

			if (Peer.getMcListener().getNumStoredConfirmsFor(chunk.getID()) < replicationDegree) {
				attempt++;

				if (attempt > MAX_ATTEMPTS)
					done = true;
				else
					waitingTime *= 2;
			} else
				done = true;
		}

		Peer.getMcListener().stopSavingStoredConfirmsFor(chunk.getID());

		Peer.getChunkDB().addRestorableFile(file.getName(),
				chunk.getID().getFileID());
	}

}
