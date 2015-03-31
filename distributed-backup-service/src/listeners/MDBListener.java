package listeners;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import peer.Peer;
import service.Chunk;
import service.MessageType;
import service.Protocol;

public class MDBListener extends SocketListener {

	public MDBListener(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) throws IOException {
		System.out.println("MDB LISTENER HANDLER");

		ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		String header = reader.readLine();
		String[] headerTokens = header.split("[ ]+");

		int bodyStartIndex = header.getBytes().length + 2
				* Protocol.CRLF.getBytes().length;
		byte[] body = Arrays.copyOfRange(packet.getData(), bodyStartIndex,
				packet.getLength());

		System.out.println("MDB: " + header);

		MessageType messageType = MessageType.valueOf(headerTokens[0]);

		switch (messageType) {

		// 3.2 Chunk backup subprotocol

		case PUTCHUNK:
			Chunk chunk = new Chunk(headerTokens[2],
					Integer.parseInt(headerTokens[3]),
					Integer.parseInt(headerTokens[4]), body);

			try {
				FileOutputStream out = new FileOutputStream(chunk.getFileID());
				out.write(chunk.getData());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// send control message
			String msg = MessageType.STORED + " " + Protocol.VERSION;
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
