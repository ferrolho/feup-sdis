package peer;

import java.io.IOException;

import screens.CanvasScreen;
import utils.Curve;
import utils.Utils;
import commands.Command;
import commands.CommandType;

public class Forwarder {

	private final CanvasScreen canvasScreen;

	public Forwarder(CanvasScreen canvasScreen) {
		this.canvasScreen = canvasScreen;
	}

	public void sendJOIN(String ip) throws IOException {
		Peer host = new Peer(ip);

		host.oos.writeObject(new Command(host));
		Utils.log("Sent JOIN");

		canvasScreen.game.peers.add(host);
		Utils.log("Added host to peers array");
	}

	public void sendGET_PEERS() throws IOException {
		Peer host = canvasScreen.game.peers.get(0);

		host.oos.writeObject(new Command(CommandType.GET_PEERS));
		Utils.log("Sent GET_PEERS");
	}

	public void sendCURVE(Curve currentCurve, Peer peer) {
		try {
			// send curve
			peer.oos.writeObject(new Command(currentCurve));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("CanvasScreen.sendCURVE");
		}
	}

}
