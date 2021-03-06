package service;

import chunk.Chunk;
import chunk.ChunkID;

public interface Protocol {

	public static final String VERSION = "1.0";

	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = CR + LF;

	// 3.2 Chunk backup subprotocol

	void sendPUTCHUNK(Chunk chunk);

	void sendSTORED(ChunkID chunkID);

	// 3.3 Chunk restore protocol

	void sendGETCHUNK(ChunkID chunkID);

	void sendCHUNK(Chunk chunk);

	// 3.4 File deletion subprotocol

	void sendDELETE(String fileID);

	// 3.5 Space reclaiming subprotocol

	void sendREMOVED(ChunkID chunkID);

}
