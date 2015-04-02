package peer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.Arrays;

import service.Chunk;
import service.MessageType;
import service.Protocol;
import service.Utils;

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

		Chunk chunk = new Chunk(headerTokens[2],
				Integer.parseInt(headerTokens[3]),
				Integer.parseInt(headerTokens[4]), body);

		try {
			// do not write to disk a chunk that already exists
			if (!Utils.fileExists(chunk.getFileID())) {
				// save chunk to disk
				FileOutputStream out = new FileOutputStream(chunk.getFileID());
				out.write(chunk.getData());
				out.close();
			}

			// random delay between 0 and 400ms
			Thread.sleep(Utils.random.nextInt(400));

			// send stored chunk confirmation
			Peer.synchedHandler.storeChunk(chunk);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storedHandler() {
		System.out.println("STORED HANDLR");
	}

	private boolean extractHeader() {
		ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		try {
			header = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		headerTokens = header.split("[ ]+");

		System.out.println(header);

		return true;
	}

	private void extractBody() {
		int bodyStartIndex = header.getBytes().length + 2
				* Protocol.CRLF.getBytes().length;

		body = Arrays.copyOfRange(packet.getData(), bodyStartIndex,
				packet.getLength());
	}

}
