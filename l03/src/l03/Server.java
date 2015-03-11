package l03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	private static int port;
	private static HashMap<String, String> plates;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// create database
		plates = new HashMap<String, String>();

		// open server socket
		System.out.println("Opening server socket...");

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		System.out.println("----------------------------");

		// repl
		boolean done = false;
		while (!done) {
			// receive request
			System.out.println("WAITING FOR REQUEST...");

			Socket socket = null;

			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed: " + port);
				System.exit(1);
			}

			// open streams
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			String request = in.readLine();
			System.out.print(request + " :: ");

			// process request
			String[] tokens = request.split(Utils.SEPARATOR);
			if (tokens.length < 2) {
				System.err.println("Received bad request");
				System.out.println();
				continue;
			}

			RequestType oper = RequestType.REGISTER.toString()
					.equals(tokens[0]) ? RequestType.REGISTER
					: RequestType.LOOKUP;
			String plate = tokens[1], owner;

			String response = "-response string not defined-";
			switch (oper) {
			case LOOKUP:
				if (plates.containsKey(plate)) {
					owner = plates.get(plate);
					response = owner;
				} else {
					response = "NOT_FOUND";
				}

				break;

			case REGISTER:
				owner = tokens[2];

				if (plates.containsKey(plate)) {
					response = "-1";
				} else {
					plates.put(plate, owner);
					response = Integer.toString(plates.size());
				}

				break;

			default:
				break;
			}

			// send response
			out.println(response);

			System.out.println(response);
			System.out.println();

			// close streams
			out.close();
			in.close();

			// close socket
			socket.close();
		}

		System.out.println("----------------------------");

		// close server socket
		System.out.println("Closing server socket...");
		serverSocket.close();

		System.out.println("Server terminated.");
		System.out.println("----------------------------");
	}

	private static boolean validArgs(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage:");
			System.out.println("\tjava Server <port>");

			return false;
		} else {
			System.out.println("----------------------------");
			port = Integer.parseInt(args[0]);
			System.out.println("Port: " + port);

			return true;
		}
	}

}
