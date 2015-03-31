package service;

public class Chunk {

	private String fileID;
	private int chunkNo;

	private int replicationDegree;
	private byte[] data;

	public Chunk(String fileID, int chunkNo, int replicationDegree, byte[] data) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;

		this.replicationDegree = replicationDegree;
		this.data = data;
	}

	public String getFileID() {
		return fileID;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public byte[] getData() {
		return data;
	}

}
