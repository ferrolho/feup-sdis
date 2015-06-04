package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import screens.CanvasScreen;
import utils.Curve;
import utils.Utils;

import commands.Command;

public class Listener implements Runnable {

	private final CanvasScreen canvasScreen;

	private ServerSocket serverSocket;

	public Listener(final CanvasScreen canvasScreen) {
		this.canvasScreen = canvasScreen;
	}

	@Override
	public void run() {
		try {
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void listen() throws IOException {
		try {
			serverSocket = new ServerSocket(canvasScreen.game.listenerPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not listen.");
			System.exit(-1);
		}

		// repl
		boolean done = false;
		while (!done) {
			Socket socket = null;

			// accept client
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed");
				System.exit(1);
			}

			// open streams
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			// receive curve
			try {
				Command command = (Command) ois.readObject();

				switch (command.getType()) {
				case CURVE:
					Curve curve = command.getCurve();

					System.out.println("got curve");
					boolean curveAdded = false;
					while (!curveAdded) {

						if (canvasScreen.isRedrawing()) {
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							// only add curve if not redrawing
							canvasScreen.drawing.add(curve);
							curveAdded = true;
						}
					}

					canvasScreen.redraw();
					break;

				case GET_PEERS:
					// open socket
					// String tempIP = command.getOriginIP();
					// System.out.println(tempIP);
					// Socket tempsocket = new Socket(tempIP, 8008);
					System.out
							.println(socket.getInetAddress().getHostAddress());

					// open streams
					ObjectOutputStream oos = new ObjectOutputStream(
							socket.getOutputStream());

					// send curve
					ArrayList<PeerID> peers = new ArrayList<PeerID>(
							canvasScreen.game.peers);
					peers.add(new PeerID(Utils.getIPv4(),
							canvasScreen.game.listenerPort));
					oos.writeObject(new Command(peers));

					// close stream
					oos.close();

					// close socket
					// tempsocket.close();
					System.out.println("peers sent");
					break;

				case JOIN:
					PeerID peerID = command.getPeer();
					canvasScreen.game.peers.add(peerID);
					break;

				case PEERS:
					canvasScreen.game.peers = command.getPeers();
					System.out.println("Peers received: "
							+ canvasScreen.game.peers);
					break;

				case PULL_DRAWING:
					canvasScreen.drawing = command.getDrawing();
					break;

				default:
					break;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// close stream
			ois.close();

			// close socket
			socket.close();
		}

		// close server socket
		serverSocket.close();

		System.out.println("Server terminated.");
	}
}
