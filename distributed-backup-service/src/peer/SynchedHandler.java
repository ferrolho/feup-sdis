package peer;

import java.io.IOException;
import java.net.DatagramPacket;

import listeners.Channel;
import listeners.MCListener;
import listeners.MDBListener;
import listeners.MDRListener;

public class SynchedHandler {

	public MCListener mcListener;
	public MDBListener mdbListener;
	public MDRListener mdrListener;

	public SynchedHandler(MCListener mcListener, MDBListener mdbListener,
			MDRListener mdrListener) {
		this.mcListener = mcListener;
		this.mdbListener = mdbListener;
		this.mdrListener = mdrListener;
	}

	public void sendPacketToChannel(byte[] buf, Channel channel) {
		switch (channel) {
		case MC:
			sendControlPacket(buf);
			break;

		case MDB:
			sendDataBackupPacket(buf);
			break;

		case MDR:
			sendDataRestorePacket(buf);
			break;

		default:
			break;
		}
	}

	private synchronized void sendControlPacket(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mcListener.address, mcListener.port);

		try {
			mcListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendDataBackupPacket(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mdbListener.address, mdbListener.port);

		try {
			mdbListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendDataRestorePacket(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mdrListener.address, mdrListener.port);

		try {
			mdrListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
