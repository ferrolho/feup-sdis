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

	public void sendPacketToChannel(DatagramPacket packet, Channel channel) {
		switch (channel) {
		case MC:
			sendControlPacket(packet);
			break;

		case MDB:
			sendDataBackupPacket(packet);
			break;

		case MDR:
			sendDataRestorePacket(packet);
			break;

		default:
			break;
		}
	}

	private synchronized void sendControlPacket(DatagramPacket packet) {
		try {
			mcListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendDataBackupPacket(DatagramPacket packet) {
		try {
			mdbListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendDataRestorePacket(DatagramPacket packet) {
		try {
			mdrListener.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
