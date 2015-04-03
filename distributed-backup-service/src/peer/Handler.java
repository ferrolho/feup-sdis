package peer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.Arrays;

import service.ChunkID;
import service.HeaderField;
import service.MessageType;
import service.Protocol;
import utils.FileUtils;
import utils.Utils;

public class Handler implements Runnable {

	private DatagramPacket packet;

	private String header;
	private String[] headerTokens;

	private byte[] body;

	public Handler(DatagramPacket packet) {
		this.packet = packet;

		header = null;
		headerTokens = null;

		body = null;
	}

	public void run() {
		if (!extractHeader())
			return;

		MessageType messageType = MessageType.valueOf(headerTokens[0]);

		switch (messageType) {

		// 3.2 Chunk backup subprotocol

		case PUTCHUNK:
			putChunkHandler();
			break;

		case STORED:
			storedHandler();
			break;

		// 3.3 Chunk restore protocol

		case GETCHUNK:
			break;

		case CHUNK:
			break;

		// 3.4 File deletion subprotocol

		case DELETE:
			break;

		// 3.5 Space reclaiming subprotocol

		case REMOVED:
			break;

		default:
			break;
		}
	}

	private void putChunkHandler() {
		extractBody();

		ChunkID chunkID = new ChunkID(headerTokens[HeaderField.FILE_ID],
				Integer.parseInt(headerTokens[HeaderField.CHUNK_NO]));

		try {
			// do not write to disk a chunk that already exists
			if (!FileUtils.fileExists(chunkID.toString())) {
				// save chunk to disk
				FileOutputStream out = new FileOutputStream(chunkID.toString());
				out.write(body);
				out.close();

				// update database
				Peer.getChunkDB().addChunk(chunkID);
				Peer.saveChunkDB();
			}

			// random delay between 0 and 400ms
			Thread.sleep(Utils.random.nextInt(400));

			// send stored chunk confirmation
			Peer.synchedHandler.sendSTORED(chunkID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storedHandler() {
		ChunkID chunkID = new ChunkID(headerTokens[HeaderField.FILE_ID],
				Integer.parseInt(headerTokens[HeaderField.CHUNK_NO]));

		PeerID senderID = new PeerID(packet.getAddress(), packet.getPort());

		Peer.getChunkDB().addChunkMirror(chunkID, senderID);

		System.out.println(Peer.getChunkDB());

		Peer.getMcListener().processStoredConfirm(chunkID, senderID);
	}

	private boolean extractHeader() {
		ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		try {
			header = reader.readLine();
			System.out.println(header);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		headerTokens = header.split("[ ]+");

		return true;
	}

	private void extractBody() {
		int bodyStartIndex = header.getBytes().length + 2
				* Protocol.CRLF.getBytes().length;

		body = Arrays.copyOfRange(packet.getData(), bodyStartIndex,
				packet.getLength());
	}

}
