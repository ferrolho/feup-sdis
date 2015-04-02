package peer;

import java.io.IOException;
import java.net.DatagramPacket;

import service.Chunk;
import service.ChunkID;
import service.MessageType;
import service.Protocol;
import service.Utils;

public class SynchedHandler implements Protocol {

	@Override
	public void sendPUTCHUNK(Chunk chunk) {
		String header = MessageType.PUTCHUNK + " " + Protocol.VERSION;
		header += " " + chunk.getChunkID().getFileID();
		header += " " + chunk.getChunkID().getChunkNo();
		header += " " + chunk.getReplicationDegree();
		header += " " + Protocol.CRLF;
		header += Protocol.CRLF;

		sendPacketToMDB(Utils.concatBytes(header.getBytes(), chunk.getData()));
	}

	@Override
	public void sendSTORED(ChunkID chunkID) {
		String header = MessageType.STORED + " " + Protocol.VERSION;
		header += " " + chunkID.getFileID();
		header += " " + chunkID.getChunkNo();
		header += " " + Protocol.CRLF;
		header += Protocol.CRLF;

		sendPacketToMC(header.getBytes());
	}

	@Override
	public void sendGETCHUNK() {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendCHUNK() {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendDELETE() {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendREMOVED() {
		// TODO Auto-generated method stub
	}

	private synchronized void sendPacketToMC(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				Peer.getMcListener().address, Peer.getMcListener().port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendPacketToMDB(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				Peer.getMdbListener().address, Peer.getMdbListener().port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendPacketToMDR(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				Peer.getMdrListener().address, Peer.getMdrListener().port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
