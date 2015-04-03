package chunk;

import java.io.Serializable;

public class ChunkID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileID;
	private int chunkNo;

	public ChunkID(String fileID, int chunkNo) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
	}

	public String getFileID() {
		return fileID;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	@Override
	public String toString() {
		return fileID + "-" + chunkNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = prime * result + chunkNo;

		result = prime * result + ((fileID == null) ? 0 : fileID.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		ChunkID other = (ChunkID) obj;

		if (chunkNo != other.chunkNo)
			return false;

		if (fileID == null) {
			if (other.fileID != null)
				return false;
		} else if (!fileID.equals(other.fileID))
			return false;

		return true;
	}

}
