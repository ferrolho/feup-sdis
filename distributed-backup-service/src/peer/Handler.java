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

public class Handler extends Thread {

	private DatagramPacket packet;

	private String header;
	private String[] headerTokens;

	private byte[] body;

	public Handler(DatagramPacket packet) {
		this.packet = packet;

		if (!extractHeader())
			return;

		extractBody();
	}

	public void run() {
		if (header == null)
			return;

		MessageType messageType = MessageType.valueOf(headerTokens[0]);

		switch (messageType) {

		// 3.2 Chunk backup subprotocol

		case PUTCHUNK:
			putChunkHandler();
			break;

		case STORED:
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
		Chunk chunk = new Chunk(headerTokens[2],
				Integer.parseInt(headerTokens[3]),
				Integer.parseInt(headerTokens[4]), body);

		try {
			FileOutputStream out = new FileOutputStream(chunk.getFileID());
			out.write(chunk.getData());
			out.close();

			Thread.sleep(Utils.randInt(0, 400));

			Peer.synchedHandler.storeChunk(chunk);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		System.out.println("HANDLER: " + header);

		return true;
	}

	private void extractBody() {
		int bodyStartIndex = header.getBytes().length + 2
				* Protocol.CRLF.getBytes().length;

		body = Arrays.copyOfRange(packet.getData(), bodyStartIndex,
				packet.getLength());
	}

}
