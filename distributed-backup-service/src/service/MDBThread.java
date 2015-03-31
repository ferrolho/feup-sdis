package service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MDBThread extends Thread {

	public MulticastSocket socket;

	private InetAddress address;
	private int port;

	public MDBThread(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public void run() {
		try {
			// multicast data backup channel
			socket = new MulticastSocket(port);

			socket.setLoopbackMode(true);
			socket.setTimeToLive(1);

			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] buf = new byte[64000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		boolean done = false;
		while (!done) {
			try {
				// receive request
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String request = new String(packet.getData(), 0, packet.getLength());

			// process request
			String[] requestTokens = request.split("[" + Protocol.CRLF + "]+",
					2);

			String header = requestTokens[0];
			String[] headerTokens = header.split("[ ]+");

			MessageType messageType = MessageType.valueOf(headerTokens[0]);

			System.out.println("MDB: " + header);

			switch (messageType) {

			// 3.2 Chunk backup subprotocol

			case PUTCHUNK:
				String body = requestTokens[1];

				Chunk chunk = new Chunk(headerTokens[2],
						Integer.parseInt(headerTokens[3]),
						Integer.parseInt(headerTokens[4]), body);

				byte[] bytes = body.getBytes(StandardCharsets.ISO_8859_1);

				try {
					FileOutputStream out = new FileOutputStream(
							chunk.getFileID());

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

				packet = new DatagramPacket(msg.getBytes(),
						msg.getBytes().length, address, port);

				Peer.synchedHandler.sendControlMessage(packet);

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

		if (socket != null)
			socket.close();
	}

}
