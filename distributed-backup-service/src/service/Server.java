package service;

public class Server {

	private static String mcAddressStr;
	private static int mcPort;

	private static String mdbAddressStr;
	private static int mdbPort;

	private static String mdrAddressStr;
	private static int mdrPort;

	public static void main(String[] args) {
		if (!validArgs(args))
			return;

		System.out.println("- done -");
	}

	private static boolean validArgs(String[] args) {
		if (args.length != 6) {
			System.out.println("Usage:");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");

			return false;
		} else {
			mcAddressStr = args[0];
			mcPort = Integer.parseInt(args[1]);

			mdbAddressStr = args[2];
			mdbPort = Integer.parseInt(args[3]);

			mdrAddressStr = args[4];
			mdrPort = Integer.parseInt(args[5]);

			return true;
		}
	}

}
