package initiators;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import peer.Peer;
import storage.FileManager;
import utils.Log;
import utils.Utils;
import chunk.Chunk;
import database.FileInfo;

public class BackupInitiator implements Runnable {

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

				Thread t = new Thread(new BackupChunkInitiator(chunk));
				t.start();

				/*
				 * TODO can chunks be backed up in parallel? not working for big
				 * files...
				 */
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Peer.getDatabase().addRestorableFile(file.getName(),
					new FileInfo(fileID, numChunks));
		} catch (FileNotFoundException e) {
			Log.error("file not found");
		}
	}

}
