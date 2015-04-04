package initiators;

import java.io.File;
import java.io.IOException;

import peer.Peer;
import utils.FileManager;
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
		if (Peer.getChunkDB().fileHasBeenBackedUp(file.getName())) {
			FileInfo fileInfo = Peer.getChunkDB().getFileInfo(file.getName());

			byte[] fileData = new byte[0];

			for (int i = 0; i < fileInfo.getNumChunks(); i++) {
				Peer.getMdrListener().prepareToReceiveFileChunks(
						fileInfo.getFileID());

				Peer.commandForwarder.sendGETCHUNK(new ChunkID(fileInfo
						.getFileID(), i));

				Chunk chunk = Peer.getMdrListener().consumeChunk(
						fileInfo.getFileID());

				fileData = Utils.concatBytes(fileData, chunk.getData());

				Peer.getMdrListener()
						.stopSavingFileChunks(fileInfo.getFileID());
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
