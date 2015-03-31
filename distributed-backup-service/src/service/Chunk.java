package service;

public class Chunk {

	private String fileID;
	private int chunkNo;

	private int replicationDegree;
	private byte[] data;
	private String dataStr;

	public Chunk(String fileID, int chunkNo, int replicationDegree,
			byte[] data, String dataStr) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;

		this.replicationDegree = replicationDegree;
		this.data = data;
		this.dataStr = dataStr;
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

	public String getDataStr() {
		return dataStr;
	}

}
