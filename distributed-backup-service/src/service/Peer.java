package service;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements RMIService {

	private static String remoteObjectName = "test";

	private static InetAddress mcAddress;
	private static int mcPort;

	private static InetAddress mdbAddress;
	private static int mdbPort;

	private static InetAddress mdrAddress;
	private static int mdrPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		Peer peer = new Peer();
		RMIService rmiService = (RMIService) UnicastRemoteObject.exportObject(
				peer, 0);

		// Bind the remote object's stub in the registry
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(remoteObjectName, rmiService);

		System.out.println("- Server ready -");

		// multicast control channel
		MulticastSocket mcSocket = new MulticastSocket();
		mcSocket.setTimeToLive(1);

		// multicast data backup channel
		MulticastSocket mdbSocket = new MulticastSocket();
		mdbSocket.setTimeToLive(1);

		// multicast data restore channel
		MulticastSocket mdrSocket = new MulticastSocket();
		mdrSocket.setTimeToLive(1);

		String test;
		DatagramPacket packet;

		test = "mc test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mcAddress, mcPort);
		mcSocket.send(packet);

		test = "mdb test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mdbAddress, mdbPort);
		mdbSocket.send(packet);

		test = "mdr test";
		packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
				mdrAddress, mdrPort);
		mdrSocket.send(packet);

		// repl was here

		mcSocket.close();
		mdbSocket.close();
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

	@Override
	public void backup(File file, int replicationDegree) throws RemoteException {
		System.out.println("backing up " + file.getName() + " "
				+ replicationDegree);

		System.out.println("fileID: " + Utils.getFileID(file));
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
