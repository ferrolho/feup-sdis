package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements Protocol, RMIService {

	private static String remoteObjectName = "test";

	private static InetAddress mcAddress;
	private static int mcPort;
	private static MCThread mcThread;

	private static InetAddress mdbAddress;
	private static int mdbPort;
	private static MDBThread mdbThread;

	private static MulticastSocket mdrSocket;
	private static InetAddress mdrAddress;
	private static int mdrPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		initRMI();

		mcThread = new MCThread(mcAddress, mcPort);
		mcThread.start();

		mdbThread = new MDBThread(mdbAddress, mdbPort, mcThread);
		mdbThread.start();

		// multicast data restore channel
		mdrSocket = new MulticastSocket(mdrPort);
		mdrSocket.setLoopbackMode(true);
		mdrSocket.setTimeToLive(1);
		mdrSocket.joinGroup(mdrAddress);

		System.out.println("- Server ready -");

		mdrSocket.close();
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		if (args.length != 0 && args.length != 6) {
			System.out.println("Usage:");
			System.out.println("\tjava Server");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");

			return false;
		} else if (args.length == 0) {
			mcAddress = InetAddress.getByName("224.0.0.0");
			mcPort = 8000;

			mdbAddress = InetAddress.getByName("224.0.0.0");
			mdbPort = 8001;

			mdrAddress = InetAddress.getByName("224.0.0.0");
			mdrPort = 8002;

			return true;
		} else {
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);

			return true;
		}
	}

	private static void initRMI() {
		Peer peer = new Peer();

		try {
			RMIService rmiService = (RMIService) UnicastRemoteObject
					.exportObject(peer, 0);

			LocateRegistry.getRegistry().rebind(remoteObjectName, rmiService);
		} catch (RemoteException e) {
			Utils.printError("Could not bind to rmiregistry");
		}
	}

	@Override
	public void putChunk(Chunk chunk) {
		String msg;

		// header
		msg = MessageType.PUTCHUNK + " " + Protocol.VERSION;
		msg += " " + chunk.getFileID();
		msg += " " + chunk.getChunkNo();
		msg += " " + chunk.getReplicationDegree();
		msg += " " + Protocol.CRLF;

		msg += Protocol.CRLF;

		// body
		msg += chunk.getDataStr();

		DatagramPacket packet = new DatagramPacket(msg.getBytes(),
				msg.getBytes().length, mdbAddress, mdbPort);

		try {
			mdbThread.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void storeChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void getChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void backup(File file, int replicationDegree) {
		try {
			Chunk chunk = new Chunk(Utils.getFileID(file), 0,
					replicationDegree, Utils.getFileDataStr(file));

			// TODO improve this method to split files

			putChunk(chunk);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(File file) throws RemoteException {
		System.out.println("deleting " + file.getName());
	}

	@Override
	public void free(int kbyte) throws RemoteException {
		System.out.println("freeing " + kbyte + "kbyte");
	}

	@Override
	public void restore(File file) throws RemoteException {
		System.out.println("restoring " + file.getName());
	}

}
