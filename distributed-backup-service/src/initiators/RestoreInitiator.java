package initiators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import peer.Peer;
import storage.FileManager;
import utils.Log;
import utils.Utils;
import chunk.Chunk;
import chunk.ChunkID;
import database.FileInfo;

public class RestoreInitiator implements Runnable {

	private File file;

	public RestoreInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		if (Peer.getDatabase().fileHasBeenBackedUp(file.getName())) {
			FileInfo fileInfo = Peer.getDatabase().getFileInfo(file.getName());

			Peer.getMdrListener().prepareToReceiveFileChunks(
					fileInfo.getFileID());

			/*
			 * Requesting chunks
			 */
			ArrayList<Chunk> chunks = new ArrayList<Chunk>();

			for (int i = 0; i < fileInfo.getNumChunks(); i++) {
				Peer.getCommandForwarder().sendGETCHUNK(
						new ChunkID(fileInfo.getFileID(), i));

				Chunk chunk = Peer.getMdrListener().consumeChunk(
						fileInfo.getFileID());

				chunks.add(chunk);
			}

			Peer.getMdrListener().stopSavingFileChunks(fileInfo.getFileID());

			/*
			 * Restoring file data
			 */
			byte[] fileData = new byte[0];

			for (int i = 0; i < fileInfo.getNumChunks(); i++) {
				Chunk chunkI = null;

				for (Chunk chunk : chunks) {
					if (chunk.getID().getChunkNo() == i) {
						chunkI = chunk;
						break;
					}
				}

				if (chunkI == null)
					Log.error("Unexpected error! Missing file chunk.");

				fileData = Utils.concatBytes(fileData, chunkI.getData());
			}

			try {
				FileManager.saveRestoredFile(file.getName(), fileData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.error("No back up found. The requested file can not be restored.");
		}
	}

}
