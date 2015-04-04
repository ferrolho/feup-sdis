package database;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileID;
	private int numChunks;

	public FileInfo(String fileID, int numChunks) {
		this.fileID = fileID;
		this.numChunks = numChunks;
	}

	public String getFileID() {
		return fileID;
	}

	public int getNumChunks() {
		return numChunks;
	}

	@Override
	public String toString() {
		return fileID + "[" + numChunks + "]";
	}

}
