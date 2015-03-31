package peer;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import service.Commands;
import service.RMIService;
import service.Utils;

public class TestClient {

	private static final String hostname = "localhost";
	private static final String remoteObjectName = "test";

	private static String command;
	private static File file;
	private static int replicationDegree, kbyte;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			RMIService peer = (RMIService) registry.lookup(remoteObjectName);

			switch (command) {
			case Commands.BACKUP:
				peer.backup(file, replicationDegree);
				break;

			case Commands.DELETE:
				peer.delete(file);
				break;

			case Commands.FREE:
				peer.free(kbyte);
				break;

			case Commands.RESTORE:
				peer.restore(file);
				break;

			default:
				break;
			}
		} catch (RemoteException e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 1) {
			Commands.printUsage();

			return false;
		}

		command = args[0];

		switch (command) {
		case Commands.BACKUP:
			if (args.length != Commands.BACKUP_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + Commands.BACKUP_USAGE);

				return false;
			}

			if (!validFile(args[1]))
				return false;

			if (!validReplicationDegree(args[2]))
				return false;

			break;

		case Commands.DELETE:
			if (args.length != Commands.DELETE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + Commands.DELETE_USAGE);

				return false;
			}

			if (!validFile(args[1]))
				return false;

			break;

		case Commands.FREE:
			if (args.length != Commands.FREE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + Commands.FREE_USAGE);

				return false;
			}

			if (!validKbyte(args[1]))
				return false;

			break;

		case Commands.RESTORE:
			if (args.length != Commands.RESTORE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + Commands.RESTORE_USAGE);

				return false;
			}

			if (!validFile(args[1]))
				return false;

			break;

		default:
			Utils.printError("Unknown command");

			Commands.printUsage();

			return false;
		}

		return true;
	}

	private static boolean validFile(String fileName) {
		file = new File(fileName);

		if (!file.exists() || !file.isFile()) {
			Utils.printError(file.getAbsolutePath() + " is not a valid file");

			return false;
		}

		return true;
	}

	private static boolean validReplicationDegree(String replicationDegreeStr) {
		try {
			replicationDegree = Integer.parseInt(replicationDegreeStr);
		} catch (NumberFormatException e) {
			Utils.printError("Replication degree must be a valid integer");

			e.printStackTrace();

			return false;
		}

		return true;
	}

	private static boolean validKbyte(String kbyteStr) {
		try {
			kbyte = Integer.parseInt(kbyteStr);
		} catch (NumberFormatException e) {
			Utils.printError("The space to be freed must be a valid integer");

			e.printStackTrace();

			return false;
		}

		return true;
	}

}
