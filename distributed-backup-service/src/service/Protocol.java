package service;

public interface Protocol {

	public static final String VERSION = "1.0";

	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = CR + LF;

	// 3.2 Chunk backup subprotocol

	void putChunk(Chunk chunk);

	void confirmChunk();

	// 3.3 Chunk restore protocol

	void getChunk();

	void sendChunk();

	// 3.4 File deletion subprotocol

	void deleteChunk();

	// 3.5 Space reclaiming subprotocol

	void removeChunk();

}
