import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Server {

	public static void main(String[] args) throws IOException {
		System.out.println("Hello");

		MulticastSocket socket = new MulticastSocket();
		socket.setSoTimeout(1000);
		socket.setTimeToLive(1);

		// TEST
		InetAddress address2 = InetAddress.getLocalHost();
		System.out.println("LOCALHOST: " + address2);
		packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
				address2, multicastPort);
		socket.send(packet);
		socket.receive(packet);
		System.out.println("--- TESTE" + packet.getAddress());
	}

}
