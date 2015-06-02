package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import screens.CanvasScreen;
import utils.Curve;

public class Listener implements Runnable {

	private final CanvasScreen canvasScreen;

	private ServerSocket serverSocket;

	public Listener(final CanvasScreen canvasScreen) {
		this.canvasScreen = canvasScreen;
	}

	@Override
	public void run() {
		try {
			temp();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void temp() throws IOException {
		try {
			serverSocket = new ServerSocket(canvasScreen.game.peerPorts.get(0));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not listen on port: "
					+ canvasScreen.game.peerPorts.get(0));
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
				System.err.println("Accept failed: "
						+ canvasScreen.game.peerPorts.get(0));
				System.exit(1);
			}

			// open streams
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			// receive curve
			try {
				Curve curve = (Curve) ois.readObject();
				canvasScreen.drawing.add(curve);

				canvasScreen.redraw();
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
