package l04;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	private static String hostName, remoteObjectName;
	private static String oper, plate, owner;

	public static void main(String[] args) {
		if (!validArgs(args))
			return;

		try {
			Registry registry = LocateRegistry.getRegistry(hostName);
			PlateManager server = (PlateManager) registry
					.lookup(remoteObjectName);

			switch (oper) {
			case "lookup":
				String lookupRes = server.lookup(plate);
				System.out.println("lookup " + plate + " :: " + lookupRes);
				break;

			case "register":
				int registerRes = server.register(plate, owner);
				System.out.println("register " + plate + " :: " + registerRes);
				break;
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Client <host_name> <remote_object_name> <oper> <opnd>*");

			return false;
		} else {
			hostName = args[0];
			remoteObjectName = args[1];
			oper = args[2];

			switch (oper) {
			case "lookup":
				if (args.length != 4) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <host_name> <remote_object_name> lookup <plate>");

					return false;
				} else {
					plate = args[3];
				}

				break;

			case "register":
				if (args.length != 5) {
					System.out.println("Usage:");
					System.out
							.println("\tjava Client <host_name> <remote_object_name> register <plate> <owner>");

					return false;
				} else {
					plate = args[3];
					owner = args[4];
				}

				break;
			}
		}

		return true;
	}

}
