package l05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {

	private static int port;
	private static HashMap<String, String> plates;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		// create database
		plates = new HashMap<String, String>();

		SSLServerSocket sslServerSocket = (SSLServerSocket) SSLServerSocketFactory
				.getDefault().createServerSocket(port);
		//sslServerSocket.setNeedClientAuth(true);

		boolean done = false;
		while (!done) {
			SSLSocket sslSocket = null;

			try {
				sslSocket = (SSLSocket) sslServerSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Accept failed: " + port);
				System.exit(1);
			}

			// open streams
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sslSocket.getInputStream()));
			PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);

			String request = in.readLine();
			System.out.print(request + " :: ");

			System.out.println("SSLServer: <oper> <opnd>*");

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
					response = "ERROR";
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
			sslSocket.close();
		}
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage:");
			System.out.println("\tjava SSLServer <port> <cypher-suite>*");

			return false;
		} else {
			port = Integer.parseInt(args[0]);

			return true;
		}
	}

}
