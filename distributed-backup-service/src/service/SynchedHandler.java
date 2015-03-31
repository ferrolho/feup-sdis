package service;

import java.io.IOException;
import java.net.DatagramPacket;

public class SynchedHandler {

	public MCThread mcThread;
	public MDBThread mdbThread;

	public SynchedHandler(MCThread mcThread, MDBThread mdbThread) {
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
