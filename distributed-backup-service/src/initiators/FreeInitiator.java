package initiators;

import chunk.ChunkID;
import peer.Peer;
import storage.FileManager;
import utils.Log;

public class FreeInitiator implements Runnable {

	private int amount;

	public FreeInitiator(int amount) {
		this.amount = amount;
	}

	@Override
	public void run() {
		Peer.getDisk().removeCapacity(amount);

		while (Peer.getDisk().getFreeBytes() < 0) {
			ChunkID chunkID = Peer.getDatabase().getMostBackedUpChunk();

			if (chunkID != null) {
				FileManager.deleteChunk(chunkID);

				Peer.getCommandForwarder().sendREMOVED(chunkID);
			} else {
				Log.error("There are no chunks stored. Unexpected error.");
			}
		}
	}

}
