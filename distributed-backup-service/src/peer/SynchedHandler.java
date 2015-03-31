package peer;

import java.io.IOException;
import java.net.DatagramPacket;

import listeners.MCListener;
import listeners.MDBListener;

public class SynchedHandler {

	public MCListener mcThread;
	public MDBListener mdbThread;

	public SynchedHandler(MCListener mcThread, MDBListener mdbThread) {
		this.mcThread = mcThread;
		this.mdbThread = mdbThread;
	}

	public synchronized void sendControlMessage(DatagramPacket packet) {
		try {
			mcThread.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
