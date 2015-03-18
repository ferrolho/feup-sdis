package l04;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server implements PlateManager {

	private static String remoteObjectName;

	private static HashMap<String, String> plates;

	public static void main(String args[]) {
		if (!validArgs(args))
			return;

		try {
			Server server = new Server();
			PlateManager plateManager = (PlateManager) UnicastRemoteObject
					.exportObject(server, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(remoteObjectName, plateManager);

			// create database
			plates = new HashMap<String, String>();

			System.err.println("- Server ready -");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	private static boolean validArgs(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage:");
			System.out.println("\tjava Server <remote_object_name>");

			return false;
		} else {
			remoteObjectName = args[0];
		}

		return true;
	}

	@Override
	public int register(String plate, String owner) throws RemoteException {
		int result;

		if (plates.containsKey(plate)) {
			result = -1;
		} else {
			plates.put(plate, owner);

			result = plates.size();
		}

		System.out.println("register " + plate + " " + owner + " :: " + result);

		return result;
	}

	@Override
	public String lookup(String plate) throws RemoteException {
		String owner = plates.containsKey(plate) ? plates.get(plate) : "ERROR";

		System.out.println("lookup " + plate + " :: " + owner);

		return owner;
	}

}
