package peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import listeners.MCListener;
import listeners.MDBListener;
import listeners.MDRListener;
import service.Chunk;
import service.RMIService;
import service.Utils;

public class Peer implements RMIService {

	private static final String remoteObjectName = "test";

	private static MCListener mcListener;
	private static MDBListener mdbListener;
	private static MDRListener mdrListener;

	public static SynchedHandler synchedHandler;

	private static InetAddress mcAddress;
	private static int mcPort;

	private static InetAddress mdbAddress;
	private static int mdbPort;

	private static InetAddress mdrAddress;
	private static int mdrPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		startRMI();

		mcListener = new MCListener(mcAddress, mcPort);
		mcListener.start();

		mdbListener = new MDBListener(mdbAddress, mdbPort);
		mdbListener.start();

		mdrListener = new MDRListener(mdrAddress, mdrPort);
		mdrListener.start();

		synchedHandler = new SynchedHandler(mcListener, mdbListener,
				mdrListener);

		System.out.println("- SERVER READY -");
	}

	private static void startRMI() {
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
	public void backup(File file, int replicationDegree) {
		try {
			Chunk chunk = new Chunk(Utils.getFileID(file), 0,
					replicationDegree, Utils.getFileData(file));

			// TODO improve this method to split files

			synchedHandler.putChunk(chunk);
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

}
