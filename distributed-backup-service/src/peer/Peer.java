package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import listeners.MCListener;
import listeners.MDBListener;
import listeners.MDRListener;
import service.RMIService;
import service.Utils;
import utils.Log;

public class Peer implements RMIService {

	private static final String DB_NAME = "db.data";

	private static final String remoteObjectName = "rmi-peer";

	private static volatile ChunkDB chunkDB;

	private static MulticastSocket socket;
	private static PeerID id;

	private static volatile MCListener mcListener;
	private static volatile MDBListener mdbListener;
	private static volatile MDRListener mdrListener;

	public static SynchedHandler synchedHandler;

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		if (!validArgs(args))
			return;

		loadChunkDB();

		socket = new MulticastSocket();
		id = new PeerID(Utils.getIPv4(), socket.getLocalPort());

		new Thread(mcListener).start();
		new Thread(mdbListener).start();
		new Thread(mdrListener).start();

		synchedHandler = new SynchedHandler();

		startRMI();

		System.out.println("- SERVER READY -");
	}

	private static void loadChunkDB() throws ClassNotFoundException,
			IOException {
		try {
			FileInputStream fileInputStream = new FileInputStream(DB_NAME);

			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

			chunkDB = (ChunkDB) objectInputStream.readObject();

			objectInputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("Database not found");

			chunkDB = new ChunkDB();

			saveChunkDB();

			Log.info("A new empty database has been created");
		}

	}

	public static void saveChunkDB() throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(DB_NAME);

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				fileOutputStream);

		objectOutputStream.writeObject(chunkDB);

		objectOutputStream.close();
	}

	private static void startRMI() {
		Peer peer = new Peer();

		try {
			RMIService rmiService = (RMIService) UnicastRemoteObject
					.exportObject(peer, 0);

			LocateRegistry.getRegistry().rebind(remoteObjectName, rmiService);
		} catch (RemoteException e) {
			Log.error("Could not bind to rmiregistry");
		}
	}

	@Override
	public void backup(File file, int replicationDegree) {
		new Thread(new BackupInitiator(file, replicationDegree)).start();
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
		InetAddress mcAddress, mdbAddress, mdrAddress;
		int mcPort, mdbPort, mdrPort;

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
		} else {
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);
		}

		mcListener = new MCListener(mcAddress, mcPort);
		mdbListener = new MDBListener(mdbAddress, mdbPort);
		mdrListener = new MDRListener(mdrAddress, mdrPort);

		return true;
	}

	public static ChunkDB getChunkDB() {
		return chunkDB;
	}

	public static MulticastSocket getSocket() {
		return socket;
	}

	public static PeerID getId() {
		return id;
	}

	public static MCListener getMcListener() {
		return mcListener;
	}

	public static MDBListener getMdbListener() {
		return mdbListener;
	}

	public static MDRListener getMdrListener() {
		return mdrListener;
	}

}
