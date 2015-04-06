package storage;

import java.io.Serializable;

import peer.Peer;
import utils.Log;

public class Disk implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_CAPACITY = 2048000;

	private int capacityBytes;
	private int usedBytes;

	public Disk() {
		this.capacityBytes = DEFAULT_CAPACITY;
		this.usedBytes = 0;
	}

	/**
	 * @return this disk capacity in bytes
	 */
	public synchronized int getCapacity() {
		return capacityBytes;
	}

	public synchronized int getUsedBytes() {
		return usedBytes;
	}

	public synchronized int getFreeBytes() {
		return capacityBytes - usedBytes;
	}

	public synchronized void saveFile(long bytes) {
		if (bytes > getFreeBytes()) {
			Log.error("Not enough space in disk!");
		} else {
			usedBytes += bytes;

			Peer.saveDisk();
		}
	}

	public synchronized void removeFile(long bytes) {
		if (bytes > getUsedBytes()) {
			Log.error("Removing more bytes than the ones being used!");
		} else {
			usedBytes -= bytes;

			Peer.saveDisk();
		}
	}

	public synchronized void addCapacity(int bytes) {
		capacityBytes += bytes;

		Peer.saveDisk();
	}

	public synchronized void removeCapacity(int bytes) {
		capacityBytes -= bytes;

		Peer.saveDisk();
	}

	@Override
	public String toString() {
		return "Disk [capacityBytes=" + capacityBytes + ", usedBytes="
				+ usedBytes + "]";
	}

}
