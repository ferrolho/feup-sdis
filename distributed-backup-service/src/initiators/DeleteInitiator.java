package initiators;

import java.io.File;

import peer.Peer;
import storage.FileManager;
import utils.Log;

public class DeleteInitiator implements Runnable {

	private String fileName;

	public DeleteInitiator(File file) {
		fileName = file.getName();
	}

	@Override
	public void run() {
		if (FileManager.fileExists(FileManager.FILES + fileName)) {
			FileManager.deleteFile(fileName);
			Log.info("Deleted " + fileName + " from FILES folder.");
		}

		if (Peer.getDatabase().fileHasBeenBackedUp(fileName)) {
			Log.info(fileName
					+ " was previously backed up by the network. Starting chunks deletion.");

			String fileID = Peer.getDatabase().getFileInfo(fileName).getFileID();

			Peer.getCommandForwarder().sendDELETE(fileID);

			Peer.getDatabase().removeRestorableFile(fileName);
		} else {
			Log.error(fileName + " has no chunks backed up by the network.");
		}
	}

}
