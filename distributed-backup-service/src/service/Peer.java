package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Peer {

	private static InetAddress mcAddress;
	private static int mcPort;

	private static InetAddress mdbAddress;
	private static int mdbPort;

	private static InetAddress mdrAddress;
	private static int mdrPort;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

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

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		boolean done = false;
		while (!done) {
			System.out.print("$ ");
			String command = br.readLine();

			String[] commandTokens = command.split("\\s+");
			// for (int i = 0; i < commandTokens.length; i++)
			// System.out.println(i + ": " + commandTokens[i]);

			switch (commandTokens[0]) {
			case Commands.QUIT:
			case Commands.QUIT_ALT:
				done = true;
				break;

			case Commands.BACKUP:
				System.out.println("back up a file");
				break;

			case Commands.RESTORE:
				System.out.println("restore a file");
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

	private static void printHelp() {
		System.out.println("Distributed backup service, version 1.0-alpha.");
		System.out.println("These shell commands are defined internally.");
		System.out.println("Type 'help' to see this list.");
		System.out.println();

		System.out.println(Commands.BACKUP);
		System.out.println("\tback up a file with a given replication degree");
		System.out.println();

		System.out.println(Commands.DELETE);
		System.out.println("\tdelete a file that was previously replicated");
		System.out.println();

		System.out.println(Commands.FREE);
		System.out
				.println("\tfree some disk space that is being used to store copies of chunks backed up by remote peers");
		System.out.println();

		System.out.println(Commands.HELP + ", " + Commands.HELP_ALT);
		System.out.println("\tsee this list");
		System.out.println();

		System.out.println(Commands.QUIT + ", " + Commands.QUIT_ALT);
		System.out.println("\tquit this application");
		System.out.println();

		System.out.println(Commands.RESTORE);
		System.out.println("\trestore a file that was previously replicated");
		System.out.println();
	}

}
