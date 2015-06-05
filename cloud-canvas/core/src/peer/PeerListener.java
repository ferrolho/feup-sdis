package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import screens.CanvasScreen;
import utils.Curve;
import utils.Utils;
import commands.Command;

public class PeerListener implements Runnable {

	private final CanvasScreen canvasScreen;

	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public PeerListener(CanvasScreen canvasScreen, Socket socket) {
		this.canvasScreen = canvasScreen;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());

			boolean done = false;
			while (!done)
				listen();

			ois.close();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Handler.handle");
		}
	}

	private void listen() throws IOException {
		try {
			// receive command
			Command command = (Command) ois.readObject();

			// process command according to its type
			switch (command.getType()) {
			case CURVE:
				handleCurve(command);
				break;

			case GET_PEERS:
				handleGetPeers();
				break;

			case JOIN:
				handleJoin(command);
				break;

			case PEERS:
				handlePeers(command);
				break;

			case PULL_DRAWING:
				handlePullDrawing(command);
				break;

			default:
				break;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Handler.handle");
		}
	}

	private void handleCurve(Command command) {
		Curve curve = command.getCurve();
		Utils.log("got curve");

		boolean curveAdded = false;
		while (!curveAdded) {
			// only add curve if not redrawing
			if (canvasScreen.isRedrawing()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				canvasScreen.drawing.add(curve);
				curveAdded = true;
			}
		}

		canvasScreen.scheduleRedraw();
	}

	private void handleGetPeers() throws IOException {
		// copy current peers to new array
		ArrayList<PeerID> peers = new ArrayList<PeerID>(canvasScreen.game.peers);

		// add this peer to the peers array to be sent
		peers.add(new PeerID(Utils.getIPv4(), canvasScreen.game.listenerPort));

		// send peers array
		oos.writeObject(new Command(peers));

		Utils.log("Peers sent to " + socket.getInetAddress().getHostAddress());
	}

	private void handleJoin(Command command) {
		PeerID peerID = command.getPeer();
		canvasScreen.game.peers.add(peerID);
	}

	private void handlePeers(Command command) {
		// save the received array of peers
		canvasScreen.game.peers = command.getPeers();

		Utils.log("Peers received: " + canvasScreen.game.peers);
	}

	private void handlePullDrawing(Command command) {
		canvasScreen.drawing = command.getDrawing();
	}

}
