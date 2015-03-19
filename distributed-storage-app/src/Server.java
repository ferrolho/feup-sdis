import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Server {

	public static void main(String[] args) throws IOException {

		MulticastSocket socket = new MulticastSocket();
		socket.setSoTimeout(1000);
		socket.setTimeToLive(1);

		// TEST
		InetAddress localhost = InetAddress.getLocalHost();
		System.out.println("LOCALHOST: " + localhost);

		String msg = "test";
		DatagramPacket packet = new DatagramPacket(msg.getBytes(),
				msg.getBytes().length, localhost, 8080);
		System.out.println("sending to: " + packet.getAddress());
		socket.send(packet);

		socket.receive(packet);
		System.out.println("--- TESTE" + packet.getAddress());

		socket.close();
	}
}
