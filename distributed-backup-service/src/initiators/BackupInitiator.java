package initiators;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import peer.Peer;
import utils.FileManager;
import utils.Log;
import utils.Utils;
import chunk.Chunk;
import database.FileInfo;

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
		/*
		 * TODO should we use this? we won't be able to change the rep.
		 * degree...
		 */
		// if (Peer.getChunkDB().fileHasBeenBackedUp(file.getName())) {
		// Log.error("A file with this name has alread been backed up.");
		// return;
		// }

		String fileID = Utils.getFileID(file);

		try {
			byte[] fileData = FileManager.loadFile(file);

			int numChunks = fileData.length / Chunk.MAX_SIZE + 1;

			Log.info(file.getName() + " will be splitted into " + numChunks
					+ " chunks.");

			ByteArrayInputStream stream = new ByteArrayInputStream(fileData);
			byte[] streamConsumer = new byte[Chunk.MAX_SIZE];

			for (int i = 0; i < numChunks; i++) {
				/*
				 * First step: get a chunk of the file
				 */

				byte[] chunkData;

				if (i == numChunks - 1 && fileData.length % Chunk.MAX_SIZE == 0) {
					chunkData = new byte[0];
				} else {
					int numBytesRead = stream.read(streamConsumer, 0,
							streamConsumer.length);

					chunkData = Arrays.copyOfRange(streamConsumer, 0,
							numBytesRead);
				}

				Chunk chunk = new Chunk(fileID, i, replicationDegree, chunkData);

				/*
				 * Second step: backup that chunk
				 */

				Peer.getMcListener()
						.startSavingStoredConfirmsFor(chunk.getID());

				long waitingTime = INITIAL_WAITING_TIME;
				int attempt = 0;

				boolean done = false;
				while (!done) {
					Peer.getMcListener().clearSavedStoredConfirmsFor(
							chunk.getID());

					Peer.commandForwarder.sendPUTCHUNK(chunk);

					try {
						System.out.println("Waiting for STOREDs for "
								+ waitingTime + "ms");
						Thread.sleep(waitingTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					int confirmedRepDeg = Peer.getMcListener()
							.getNumStoredConfirmsFor(chunk.getID());

					Log.info(confirmedRepDeg
							+ " peers have backed up the chunk. (desired: "
							+ replicationDegree + " )");

					if (confirmedRepDeg < replicationDegree) {
						attempt++;

						if (attempt > MAX_ATTEMPTS) {
							Log.info("Reached maximum number of attempts to backup chunk with desired replication degree.");
							done = true;
						} else {
							Log.info("Desired replication degree was not reached. Trying again...");
							waitingTime *= 2;
						}
					} else {
						Log.info("Desired replication degree reached.");
						done = true;
					}
				}

				Peer.getMcListener().stopSavingStoredConfirmsFor(chunk.getID());
			}

			Peer.getChunkDB().addRestorableFile(file.getName(),
					new FileInfo(fileID, numChunks));
		} catch (FileNotFoundException e) {
			Log.error("file not found");
		}
	}

}
