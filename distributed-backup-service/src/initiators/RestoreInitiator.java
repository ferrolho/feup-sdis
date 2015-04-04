package initiators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import peer.Peer;
import utils.FileUtils;
import chunk.Chunk;
import chunk.ChunkID;

public class RestoreInitiator implements Runnable {

	private File file;

	public RestoreInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		ChunkID chunkID = new ChunkID(FileUtils.getFileID(file), 0);

		Peer.getMdrListener().prepareToReceiveFileChunks(chunkID.getFileID());

		Peer.commandForwarder.sendGETCHUNK(chunkID);

		Chunk chunk = Peer.getMdrListener().consumeChunk(chunkID.getFileID());

		try {
			// save chunk to disk
			FileOutputStream out = new FileOutputStream(file.getName()
					+ ".bckp");
			out.write(chunk.getData());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Peer.getMdrListener().stopSavingFileChunks(chunkID.getFileID());
	}

}
