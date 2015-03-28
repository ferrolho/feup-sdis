package service;

import java.io.File;

public class TestClient implements RMIService {

	private static String command;
	private static File file;
	private static int replicationDegree, kbyte;

	public static void main(String[] args) {
		if (!validArgs(args))
			return;

		System.out.println("GOOD");
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

	@Override
	public void backup(String file, int replicationDegree) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(String file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void free(int kbytes) {
		// TODO Auto-generated method stub

	}

}
