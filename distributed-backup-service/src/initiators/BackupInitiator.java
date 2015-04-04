package initiators;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import peer.Peer;
import service.Protocol;
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

		try {
			String fileID = Utils.getFileID(file);
			byte[] fileData = FileManager.loadFile(file);
			ByteArrayInputStream stream = new ByteArrayInputStream(fileData);

			int numChunks = fileData.length / Protocol.PACKET_MAX_SIZE + 1;
			Log.info(file.getName() + " will be splitted into " + numChunks
					+ " chunks.");

			byte[] buf = new byte[Protocol.PACKET_MAX_SIZE];

			for (int i = 0, offset = 0; i < numChunks; i++) {
				byte[] chunkData;

				/*
				 * If this is the last chunk, and the file size is a multiple of
				 * the chunk size.
				 */
				if (i == numChunks - 1
						&& fileData.length % Protocol.PACKET_MAX_SIZE == 0) {
					chunkData = new byte[0];
				} else {
					int dataStart = i * Protocol.PACKET_MAX_SIZE;
					int dataEnd = dataStart + Protocol.PACKET_MAX_SIZE;

					if (dataEnd > fileData.length)
						dataEnd = fileData.length;

					int readBytes = stream.read(buf, offset,
							Protocol.PACKET_MAX_SIZE);
					offset += readBytes;

					chunkData = Arrays.copyOfRange(buf, 0, readBytes);
				}

				Chunk chunk = new Chunk(fileID, i, replicationDegree, chunkData);

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

					System.out.println(Peer.getMcListener()
							.getNumStoredConfirmsFor(chunk.getID())
							+ " of "
							+ replicationDegree + " peers stored the chunk");
					System.out.println();

					if (Peer.getMcListener().getNumStoredConfirmsFor(
							chunk.getID()) < replicationDegree) {
						attempt++;

						if (attempt > MAX_ATTEMPTS)
							done = true;
						else
							waitingTime *= 2;
					} else
						done = true;
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
