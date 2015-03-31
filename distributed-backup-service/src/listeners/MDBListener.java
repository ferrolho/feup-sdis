package listeners;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import peer.Peer;
import service.Chunk;
import service.MessageType;
import service.Protocol;

public class MDBListener extends SocketListener {

	public MDBListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) {
		System.out.println("MDB LISTENER HANDLER");

		System.out.println("TEST: " + packet.getData());

		String request = new String(packet.getData(), 0, packet.getLength());

		// process request
		String[] requestTokens = request.split("[" + Protocol.CRLF + "]+", 2);

		String header = requestTokens[0];
		String[] headerTokens = header.split("[ ]+");

		MessageType messageType = MessageType.valueOf(headerTokens[0]);

		System.out.println("MDB: " + header);

		switch (messageType) {

		// 3.2 Chunk backup subprotocol

		case PUTCHUNK:
			String bodyStr = requestTokens[1];
			byte[] body = null;

			Chunk chunk = new Chunk(headerTokens[2],
					Integer.parseInt(headerTokens[3]),
					Integer.parseInt(headerTokens[4]), body, bodyStr);

			byte[] bytes = bodyStr.getBytes(StandardCharsets.ISO_8859_1);

			try {
				FileOutputStream out = new FileOutputStream(chunk.getFileID());
				out.write(bytes);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// send control message
			String msg;
			msg = MessageType.STORED + " " + Protocol.VERSION;
			msg += " " + chunk.getFileID();
			msg += " " + chunk.getChunkNo();
			msg += " " + Protocol.CRLF;
			msg += Protocol.CRLF;

			Peer.synchedHandler.sendPacketToChannel(msg.getBytes(), Channel.MC);

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

}
