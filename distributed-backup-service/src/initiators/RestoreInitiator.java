package initiators;

import java.io.File;
import java.io.IOException;

import peer.Peer;
import utils.FileManager;
import chunk.Chunk;
import chunk.ChunkID;

public class RestoreInitiator implements Runnable {

	private File file;

	public RestoreInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		if (Peer.getChunkDB().fileHasBeenBackedUp(file.getName())) {
			String fileID = Peer.getChunkDB().getFileID(file.getName());

			ChunkID chunkID = new ChunkID(fileID, 0);

			Peer.getMdrListener().prepareToReceiveFileChunks(
					chunkID.getFileID());

			Peer.commandForwarder.sendGETCHUNK(chunkID);

			Chunk chunk = Peer.getMdrListener().consumeChunk(
					chunkID.getFileID());

			try {
				FileManager.saveRestore(file.getName(), chunk.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}

			Peer.getMdrListener().stopSavingFileChunks(chunkID.getFileID());
		}
	}

}
