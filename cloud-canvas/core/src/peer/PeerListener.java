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

	public PeerListener(CanvasScreen canvasScreen, Socket socket)
			throws IOException {
		this.canvasScreen = canvasScreen;

		this.socket = socket;
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
	}

	public PeerListener(CanvasScreen canvasScreen, Peer host) {
		this.canvasScreen = canvasScreen;

		this.socket = host.getSocket();
		ois = host.ois;
		oos = host.oos;
	}

	@Override
	public void run() {
		try {
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
			case JOIN:
				handleJoin(command);
				break;

			case GET_PEERS:
				handleGetPeers();
				break;

			case PEERS:
				handlePeers(command);
				break;

			case PULL_DRAWING:
				handlePullDrawing(command);
				break;

			case DRAWING:
				handleDrawing(command);
				break;

			case CURVE:
				handleCurve(command);
				break;

			default:
				break;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Handler.handle");
		}
	}

	private void handleJoin(Command command) throws IOException {
		Utils.log("Received JOIN");

		// read peer received with the command
		// note: this peer does not have a socket, ois, and oos set yet
		Peer peer = new Peer(command.getOriginIP());
		peer.setNetworkData(socket, ois, oos);

		canvasScreen.game.peers.add(peer);

		Utils.log("Peer " + peer + " added");
	}

	private void handleGetPeers() throws IOException {
		ArrayList<String> peersIP = new ArrayList<String>();

		// copy current peers to new array
		for (Peer peer : canvasScreen.game.peers)
			peersIP.add(peer.getIP());

		// send peers array
		oos.writeObject(new Command(peersIP));

		Utils.log("Peers sent to " + socket.getInetAddress().getHostAddress());
	}

	private void handlePeers(Command command) throws IOException {
		Utils.log("Peers received: " + command.getPeersIP());

		// save the received array of peers
		for (String peerIP : command.getPeersIP()) {
			if (peerIP.equals(Utils.getIPv4().getHostAddress()))
				continue;

			Peer peer = new Peer(peerIP);
			peer.createNetworkData();

			// add to peers array
			canvasScreen.game.peers.add(peer);

			// send JOIN
			canvasScreen.forwarder.sendJOIN(peerIP);
		}

		Utils.log("Resultant peers array: " + canvasScreen.game.peers);
	}

	private void handlePullDrawing(Command command) throws IOException {
		Utils.log("Received PULL_DRAWING");

		Peer destinyPeer = null;

		for (Peer peer : canvasScreen.game.peers) {
			if (peer.getIP().equals(command.getOriginIP())) {
				destinyPeer = peer;
				break;
			}
		}

		if (destinyPeer != null)
			canvasScreen.forwarder.sendDRAWING(canvasScreen.drawing,
					destinyPeer);
		else
			Utils.log("handlePullDrawing could not find the destiny peer to send the drawing.");
	}

	private void handleDrawing(Command command) {
		Utils.log("Received DRAWING");

		canvasScreen.drawing = command.getDrawing();
	}

	private void handleCurve(Command command) {
		Curve curve = command.getCurve();
		Utils.log("Received CURVE");

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

}
