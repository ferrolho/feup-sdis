package service;

public class Chunk {

	private String fileID;
	private int chunkNo;

	private String data;

	public Chunk(String fileID, int chunkNo, String data) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.data = data;
	}

}
