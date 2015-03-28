package l05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {

	private static String host;
	private static int port;
	private static RequestType oper;
	private static String plate, owner;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		String request = buildRequest();

		// open socket
		SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault()
				.createSocket(host, port);
		//sslSocket.setNeedClientAuth(true);

		// open streams
		PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				sslSocket.getInputStream()));

		// send request
		out.println(request);
		System.out.print(request + " :: ");

		// receive response
		String response = in.readLine();
		System.out.println(response);
		System.out.println();

		// close streams
		out.close();
		in.close();

		// close socket
		sslSocket.close();
	}

	private static boolean validArgs(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out
					.println("\tjava SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");

			return false;
		} else {
			host = args[0];
			port = Integer.parseInt(args[1]);

			String operStr = args[2];

			if (RequestType.REGISTER.toString().equals(operStr)) {
				if (args.length < 5) {
					System.out.println("Usage:");
					System.out
							.println("\tjava SSLClient <host> <port> register <plate number> <owner name> <cypher-suite>*");

					return false;
				}

				oper = RequestType.REGISTER;
				plate = args[3];
				owner = args[4];
			} else if (RequestType.LOOKUP.toString().equals(operStr)) {
				if (args.length < 4) {
					System.out.println("Usage:");
					System.out
							.println("\tjava SSLClient <host> <port> lookup <plate number> <cypher-suite>*");

					return false;
				}

				oper = RequestType.LOOKUP;
				plate = args[3];
			} else {
				System.out.println("Usage:");
				System.out
						.println("\tjava SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
				System.out.println("\t\t<oper> - register | lookup");

				return false;
			}
		}

		return true;
	}

	private static String buildRequest() {
		String request = oper.toString();

		switch (oper) {
		case LOOKUP:
			request += Utils.SEPARATOR + plate;
			break;

		case REGISTER:
			request += Utils.SEPARATOR + plate + Utils.SEPARATOR + owner;
			break;
		}

		return request;
	}

}
