package initiators;

import java.io.File;

import peer.Peer;
import utils.FileManager;
import utils.Log;
import utils.Utils;

public class DeleteInitiator implements Runnable {

	private File file;

	public DeleteInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {

		if (!FileManager.fileExists(file.getName())) {
			Log.error("file not found");
			return;
		}

		String fileID = Utils.getFileID(file);

		boolean done = false;
		while (!done) {
			if (Peer.hasChunkFromFile(fileID))
				Peer.commandForwarder.sendDELETE(fileID);
			else
				done = true;
		}
	}

}
