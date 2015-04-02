package service;

public class Chunk {

	private ChunkID chunkID;

	private int replicationDegree;

	private byte[] data;

	public Chunk(String fileID, int chunkNo, int replicationDegree, byte[] data) {
		chunkID = new ChunkID(fileID, chunkNo);

		this.replicationDegree = replicationDegree;

		this.data = data;
	}

	public ChunkID getChunkID() {
		return chunkID;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public byte[] getData() {
		return data;
	}

}
