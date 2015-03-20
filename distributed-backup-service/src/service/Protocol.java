package service;

import java.rmi.Remote;

public interface Protocol extends Remote {

	// 3.2 Chunk backup subprotocol

	void putChunk();

	void confirmChunk();

	// 3.3 Chunk restore protocol

	void getChunk();

	void sendChunk();

	// 3.4 File deletion subprotocol

	void deleteChunk();

	// 3.5 Space reclaiming subprotocol

	void removeChunk();

}
