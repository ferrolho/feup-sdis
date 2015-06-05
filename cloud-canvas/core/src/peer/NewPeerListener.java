package peer;

import java.io.IOException;
import java.net.ServerSocket;

import screens.CanvasScreen;

public class NewPeerListener implements Runnable {

	private final CanvasScreen canvasScreen;

	private ServerSocket serverSocket;

	public NewPeerListener(final CanvasScreen canvasScreen) {
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
			System.err.println("Listener.listen");
		}

		boolean done = false;
		while (!done) {
			new Thread(new PeerListener(canvasScreen, serverSocket.accept()))
					.start();
		}

		serverSocket.close();

		System.out.println("Server terminated.");
	}
}
