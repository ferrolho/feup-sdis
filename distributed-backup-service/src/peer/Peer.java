package peer;

import initiators.BackupInitiator;
import initiators.DeleteInitiator;
import initiators.SpaceInitiator;
import initiators.RestoreInitiator;

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
import service.CommandForwarder;
import service.RMIService;
import storage.Disk;
import utils.Log;
import utils.Utils;
import database.Database;

public class Peer implements RMIService {

	private static final String DISK_NAME = "disk.data";
	private static final String DB_NAME = "db.data";

	private static volatile Disk disk;
	private static volatile Database database;

	private static MulticastSocket socket;
	private static PeerID id;

	private static volatile MCListener mcListener;
	private static volatile MDBListener mdbListener;
	private static volatile MDRListener mdrListener;

	private static CommandForwarder commandForwarder;

	private static String remoteObjectName;

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		if (!validArgs(args))
			return;

		loadDisk();
		loadDatabase();

		socket = new MulticastSocket();
		id = new PeerID(Utils.getIPv4(), socket.getLocalPort());

		new Thread(mcListener).start();
		new Thread(mdbListener).start();
		new Thread(mdrListener).start();

		commandForwarder = new CommandForwarder();

		if (remoteObjectName != null)
			startRMI();

		System.out.println("- SERVER READY -");
	}

	private static void createNewDisk() {
		disk = new Disk();

		saveDisk();

		Log.info("A new disk with capacity of " + disk.getCapacity()
				+ " bytes has been created.");
	}

	private static void loadDisk() throws ClassNotFoundException, IOException {
		try {
			FileInputStream fileInputStream = new FileInputStream(DISK_NAME);

			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

			disk = (Disk) objectInputStream.readObject();

			objectInputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("Disk not found");

			createNewDisk();
		}
	}

	public static void saveDisk() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(DISK_NAME);

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					fileOutputStream);

			objectOutputStream.writeObject(disk);

			objectOutputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("Disk not found");

			createNewDisk();

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createNewDatabase() {
		database = new Database();

		saveDatabase();

		Log.info("A new empty database has been created");
	}

	private static void loadDatabase() throws ClassNotFoundException,
			IOException {
		try {
			FileInputStream fileInputStream = new FileInputStream(DB_NAME);

			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

			database = (Database) objectInputStream.readObject();

			objectInputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("Database not found");

			createNewDatabase();
		}
	}

	public static void saveDatabase() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(DB_NAME);

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					fileOutputStream);

			objectOutputStream.writeObject(database);

			objectOutputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("Database not found");

			createNewDatabase();

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void backup(File file, int replicationDegree) throws RemoteException {
		new Thread(new BackupInitiator(file, replicationDegree)).start();
	}

	@Override
	public void restore(File file) throws RemoteException {
		new Thread(new RestoreInitiator(file)).start();
	}

	@Override
	public void delete(File file) throws RemoteException {
		new Thread(new DeleteInitiator(file)).start();
	}

	@Override
	public void space(int amount) throws RemoteException {
		new Thread(new SpaceInitiator(amount)).start();
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		InetAddress mcAddress, mdbAddress, mdrAddress;
		int mcPort, mdbPort, mdrPort;

		if (args.length != 0 && args.length != 1 && args.length != 6
				&& args.length != 7) {
			System.out.println("Usage: (w/o trigger)");
			System.out.println("\tjava Server");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");
			System.out.println();
			System.out.println("Usage: (w/ trigger)");
			System.out.println("\tjava Server <RMI obj. name>");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort> <RMI obj. name>");

			return false;
		} else if (args.length == 0 || args.length == 1) {
			mcAddress = InetAddress.getByName("224.0.0.0");
			mcPort = 8000;

			mdbAddress = InetAddress.getByName("224.0.0.0");
			mdbPort = 8001;

			mdrAddress = InetAddress.getByName("224.0.0.0");
			mdrPort = 8002;

			if (args.length == 1)
				remoteObjectName = args[0];
		} else {
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);

			if (args.length == 7)
				remoteObjectName = args[6];
		}

		mcListener = new MCListener(mcAddress, mcPort);
		mdbListener = new MDBListener(mdbAddress, mdbPort);
		mdrListener = new MDRListener(mdrAddress, mdrPort);

		return true;
	}

	public static Disk getDisk() {
		return disk;
	}

	public static Database getDatabase() {
		return database;
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

	public static CommandForwarder getCommandForwarder() {
		return commandForwarder;
	}

}
