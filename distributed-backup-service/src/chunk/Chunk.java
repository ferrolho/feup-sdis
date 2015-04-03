package chunk;

public class Chunk {

	private ChunkID id;

	private int replicationDegree;

	private byte[] data;

	public Chunk(String fileID, int chunkNo, int replicationDegree, byte[] data) {
		id = new ChunkID(fileID, chunkNo);

		this.replicationDegree = replicationDegree;

		this.data = data;
	}

	public ChunkID getID() {
		return id;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public byte[] getData() {
		return data;
	}

}
