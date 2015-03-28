package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Client {

	private static InetAddress mcAddress;
	private static int mcPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		boolean done = false;
		while (!done) {
			System.out.print("$ ");
			String command = br.readLine();

			String[] commandTokens = command.split("\\s+");
			// for (int i = 0; i < commandTokens.length; i++)
			// System.out.println(i + ": " + commandTokens[i]);

			switch (commandTokens[0]) {
			case Commands.BACKUP:
				System.out.println("back up a file");

				MulticastSocket multicastSocket = new MulticastSocket(mcPort);
				multicastSocket.joinGroup(mcAddress);

				byte[] buf = new byte[256];
				DatagramPacket multicastPacket = new DatagramPacket(buf,
						buf.length);
				multicastSocket.receive(multicastPacket);

				String msg = new String(multicastPacket.getData());

				System.out.println("mgs: " + msg);

				multicastSocket.leaveGroup(mcAddress);
				multicastSocket.close();

				break;

			case Commands.DELETE:
				System.out.println("delete a file");
				break;

			case Commands.FREE:
				System.out.println("free some disk space");
				break;

			case Commands.HELP:
			case Commands.HELP_ALT:
				printHelp();
				break;

			case Commands.QUIT:
			case Commands.QUIT_ALT:
				done = true;
				break;

			case Commands.RESTORE:
				System.out.println("restore a file");
				break;

			default:
				System.out.println("Unknown command.");
				break;
			}

			/*
			 * System.out.print("Enter Integer:"); try { int i =
			 * Integer.parseInt(br.readLine()); } catch (NumberFormatException
			 * nfe) { System.err.println("Invalid Format!"); }
			 */
		}
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		// mcAddress = InetAddress.getByName(args[0]);
		// mcPort = Integer.parseInt(args[1]);

		return true;
	}

	private static void printHelp() {
		System.out.println("Distributed backup service, version 1.0-alpha.");
		System.out.println("These shell commands are defined internally.");
		System.out.println("Type 'help' to see this list.");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.BACKUP + " <file> <replication degree>");
		System.out.println();
		System.out.println("back up a file with a given replication degree");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.DELETE);
		System.out.println();
		System.out.println("delete a file that was previously replicated");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.FREE);
		System.out.println();
		System.out
				.println("free some disk space that is being used to store copies of chunks backed up by remote peers");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.HELP + ", " + Commands.HELP_ALT);
		System.out.println();
		System.out.println("see this list");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.QUIT + ", " + Commands.QUIT_ALT);
		System.out.println();
		System.out.println("quit this application");
		System.out.println("______________________________");

		System.out.println();
		System.out.println(Commands.RESTORE);
		System.out.println();
		System.out.println("restore a file that was previously replicated");
		System.out.println();
	}

}
