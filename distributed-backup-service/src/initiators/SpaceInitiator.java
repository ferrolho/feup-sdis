package initiators;

import chunk.ChunkID;
import peer.Peer;
import storage.FileManager;
import utils.Log;
import utils.Utils;

public class SpaceInitiator implements Runnable {

	private int amount;

	public SpaceInitiator(int amount) {
		this.amount = amount;
	}

	@Override
	public void run() {
		Peer.getDisk().setCapacity(amount);

		while (Peer.getDisk().getFreeBytes() < 0) {
			ChunkID chunkID = Peer.getDatabase().getMostBackedUpChunk();

			if (chunkID != null) {
				Peer.getCommandForwarder().sendREMOVED(chunkID);

				/*
				 * Listen for PUTCHUNKs after sending REMOVED.
				 */
				Peer.getMdbListener().startSavingPUTCHUNKsFor(chunkID);

				try {
					Thread.sleep(Utils.random.nextInt(500));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int numPUTCHUNKsRegisteredMeanwhile = Peer.getMdbListener()
						.getNumPUTCHUNKsFor(chunkID);

				Peer.getMdbListener().stopSavingPUTCHUNKsFor(chunkID);

				/*
				 * If no PUTCHUNKs are received after 500ms, that means the
				 * replication degree of the chunk this peer is removing is
				 * still greater or equal to the desired; else, start listening
				 * for STOREDs and only delete the chunk after a STORED has been
				 * confirmed, or maximum attempt has been reached.
				 */
				if (numPUTCHUNKsRegisteredMeanwhile != 0) {
					Peer.getMcListener().startSavingStoredConfirmsFor(chunkID);

					long waitingTime = BackupChunkInitiator.INITIAL_WAITING_TIME;
					int attempt = 0;

					boolean done = false;
					while (!done) {
						try {
							System.out.println("Waiting for STOREDs for "
									+ waitingTime + "ms");
							Thread.sleep(waitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if (Peer.getMcListener().getNumStoredConfirmsFor(
								chunkID) == 0) {
							attempt++;

							if (attempt > BackupChunkInitiator.MAX_ATTEMPTS) {
								Log.error("None of the peers has stored this chunk. Replication degree will be less than desired.");
								done = true;
							} else {
								waitingTime *= 2;
							}
						} else {
							done = true;
						}
					}

					Peer.getMcListener().stopSavingStoredConfirmsFor(chunkID);
				}

				Log.info("Deleting chunk no. " + chunkID.getChunkNo());

				FileManager.deleteChunk(chunkID);
			} else {
				Log.error("There are no chunks stored. Unexpected error.");
			}
		}
	}

}
