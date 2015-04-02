package trigger;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import service.RMIService;
import service.Utils;

public class Trigger {

	private static final String HOST = "localhost";

	private static String remoteObjectName;
	private static String command;
	private static File file;
	private static int replicationDegree, kbyte;

	private static RMIService peer;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		switch (command) {
		case TriggerCommands.BACKUP:
			peer.backup(file, replicationDegree);
			break;

		case TriggerCommands.RESTORE:
			peer.restore(file);
			break;

		case TriggerCommands.DELETE:
			peer.delete(file);
			break;

		case TriggerCommands.SPACE:
			peer.free(kbyte);
			break;

		default:
			break;
		}
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 1) {
			TriggerCommands.printUsage(Trigger.class.getName());

			return false;
		}

		remoteObjectName = args[0];
		command = args[1];

		try {
			Registry registry = LocateRegistry.getRegistry(HOST);

			peer = (RMIService) registry.lookup(remoteObjectName);
		} catch (RemoteException | NotBoundException e) {
			Utils.printError("Invalid RMI object name");

			return false;
		}

		switch (command) {
		case TriggerCommands.BACKUP:
			if (args.length != TriggerCommands.BACKUP_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + TriggerCommands.BACKUP_USAGE);

				return false;
			}

			if (!validFilePath(args[2]))
				return false;

			if (!validReplicationDegree(args[3]))
				return false;

			break;

		case TriggerCommands.RESTORE:
			if (args.length != TriggerCommands.RESTORE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + TriggerCommands.RESTORE_USAGE);

				return false;
			}

			if (!validFilePath(args[2]))
				return false;

			break;

		case TriggerCommands.DELETE:
			if (args.length != TriggerCommands.DELETE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + TriggerCommands.DELETE_USAGE);

				return false;
			}

			if (!validFilePath(args[2]))
				return false;

			break;

		case TriggerCommands.SPACE:
			if (args.length != TriggerCommands.SPACE_NUM_ARGS) {
				Utils.printError("Wrong number of arguments");

				System.out.println("Usage:");
				System.out.println("\t" + TriggerCommands.SPACE_USAGE);

				return false;
			}

			if (!validAmountOfSpace(args[2]))
				return false;

			break;

		default:
			Utils.printError("Unknown command");

			TriggerCommands.printUsage(Trigger.class.getName());

			return false;
		}

		return true;
	}

	private static boolean validFilePath(String fileName) {
		file = new File(fileName);

		if (!file.exists()) {
			Utils.printError(file.getAbsolutePath() + " does not exist");

			return false;
		} else if (!file.isFile()) {
			Utils.printError(file.getAbsolutePath()
					+ " is not a file, it is a folder");

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

	private static boolean validAmountOfSpace(String kbyteStr) {
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