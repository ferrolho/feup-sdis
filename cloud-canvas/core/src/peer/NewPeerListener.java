package peer;

import java.io.IOException;
import java.net.ServerSocket;

import screens.CanvasScreen;
import utils.Utils;

public class NewPeerListener implements Runnable {

	private final CanvasScreen canvas;

	private ServerSocket serverSocket;

	public NewPeerListener(final CanvasScreen canvas) {
		this.canvas = canvas;
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
			serverSocket = new ServerSocket(canvas.listenerPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Listener.listen");
		}

		boolean done = false;
		while (!done) {
			new Thread(new PeerListener(canvas, serverSocket.accept())).start();
			Utils.log("serverSocket.accept()");
		}

		serverSocket.close();

		System.out.println("Server terminated.");
	}

}
