package l04;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];

		try {
			Registry registry = LocateRegistry.getRegistry(host);
			PlateManager stub = (PlateManager) registry.lookup("PlateManager");
			int response = stub.register("11-22-AA", "ferrolho");
			System.out.println("response: " + response);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
