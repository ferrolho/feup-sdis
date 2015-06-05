package screens;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import launcher.CloudCanvas;
import peer.NewPeerListener;
import peer.PeerID;
import utils.Curve;
import utils.HTTPRequest;
import utils.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import commands.Command;
import commands.CommandType;

public class CanvasScreen implements Screen, InputProcessor {

	private int CANVAS_WIDTH = 400;
	private int CANVAS_HEIGHT = 400;

	public final CloudCanvas game;

	private OrthographicCamera camera;

	public Pixmap pixmap;
	public Texture texture;

	public volatile ArrayList<Curve> drawing;
	private Curve currentCurve;

	private Vector3 lastTouchPos, touchPos;
	private boolean redraw;

	private int viewportWidth, viewportHeight;

	public CanvasScreen(final CloudCanvas game) {
		this.game = game;

		Gdx.input.setInputProcessor(this);

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		pixmap = new Pixmap(CANVAS_WIDTH, CANVAS_HEIGHT, Format.RGBA8888);
		pixmap.setColor(1, 1, 1, 1);
		pixmap.fillRectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		texture = new Texture(pixmap, true);

		drawing = new ArrayList<Curve>();
		currentCurve = new Curve();

		touchPos = new Vector3();
		redraw = false;

		camera = new OrthographicCamera();
		positionCamera();

		initPeerNetwork();
	}

	private void initPeerNetwork() {
		try {
			HTTPRequest request = new HTTPRequest("/canvas/getRoomList");
			String responseStr = request.GET(Utils.UTF_8);

			if (responseStr.isEmpty())
				createRoom();
			else {
				// split responseStr into array of roomsStr
				String[] rooms = responseStr.split("\\s+");
				Utils.log("Room: " + rooms[0]);

				// get ip of the first room
				String ip = rooms[0].split(",")[1];
				Utils.log("Connecting to IP -" + ip + "-");

				joinRoom(ip);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.err
					.println("CanvasScreen.initPeerNetwork - MalformedURLException");
		} catch (IOException e) {
			System.out.println("CanvasScreen.initPeerNetwork - IOException");
			e.printStackTrace();
		}

		new Thread(new NewPeerListener(this)).start();
	}

	private void createRoom() throws IOException, MalformedURLException {
		Utils.log("Creating new room.");

		String[] paramName = { "userIp" };
		String[] paramVal = { Utils.getIPv4().getHostAddress() };

		String ret = new HTTPRequest("/canvas/createRoom").POST(paramName,
				paramVal);

		Utils.log(ret);
	}

	private void joinRoom(String ip) throws UnknownHostException, IOException {
		Socket socket = new Socket(ip, 8008);

		{
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());

			// TODO change to JOIN
			oos.writeObject(new Command(CommandType.GET_PEERS));

			oos.close();
		}

		{
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			try {
				Command command = (Command) ois.readObject();
				Utils.log("TEST: " + command.getType());

				game.peers = command.getPeers();
				Utils.log("22Peers received: " + game.peers);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			ois.close();
		}

		socket.close();
	}

	private void positionCamera() {
		camera.setToOrtho(false, viewportWidth, viewportHeight);
		camera.position.set(texture.getWidth() / 2, texture.getHeight() / 2, 0);
	}

	@Override
	public void render(float delta) {
		// clear display
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.spriteBatch.setProjectionMatrix(camera.combined);

		if (redraw) {
			for (Curve curve : drawing)
				curve.draw(pixmap);
			texture.draw(pixmap, 0, 0);

			tempsync();
		}

		// draw the canvas with the drawing
		game.spriteBatch.begin();
		game.spriteBatch.draw(texture, 0, 0);
		game.spriteBatch.end();
	}

	private synchronized void tempsync() {
		redraw = false;
		notifyAll();
	}

	@Override
	public void resize(int width, int height) {
		viewportWidth = width;
		viewportHeight = height;

		positionCamera();
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		// rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		closeSockets();

		String[] paramName = { "roomName" };
		String[] paramVal = { "Sala" };
		try {
			new HTTPRequest("/canvas/leaveRoom").POST(paramName, paramVal);
			System.out.println("left room");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("deu asneira a sair da sala");
		}
	}

	private void closeSockets() {
		for (PeerID peerID : game.peers) {
			if (peerID.socketIsSet()) {
				try {
					peerID.getSocket().close();
				} catch (IOException e) {
					System.out.println("failed to close socket at end");
				}
			}
		}
	}

	boolean ctrlIsBeingPressed = false;

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Input.Keys.CONTROL_LEFT:
		case Input.Keys.CONTROL_RIGHT:
			ctrlIsBeingPressed = true;
			break;

		case Input.Keys.Z:
			if (ctrlIsBeingPressed) {
				if (!drawing.isEmpty())
					drawing.remove(drawing.size() - 1);
				else
					System.out.println("CTRL + Z - Nothing to undo!");

				pixmap.setColor(1, 1, 1, 1);
				pixmap.fillRectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

				scheduleRedraw();
			}
			break;

		default:
			break;
		}

		return true;
	}

	public void scheduleRedraw() {
		redraw = true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Input.Keys.CONTROL_LEFT:
		case Input.Keys.CONTROL_RIGHT:
			ctrlIsBeingPressed = false;
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		temp(screenX, screenY);

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		temp(screenX, screenY);

		return true;
	}

	private void temp(int screenX, int screenY) {
		// saving previous touch position
		if (lastTouchPos != null)
			lastTouchPos.set(touchPos);

		// saving current touch position
		touchPos.set(screenX, Gdx.graphics.getHeight() - screenY, 0);
		camera.unproject(touchPos);

		// if touch has just started, there is no last touch position -> set it
		if (lastTouchPos == null) {
			lastTouchPos = new Vector3(touchPos);
			currentCurve.add(lastTouchPos.x, lastTouchPos.y);
		}

		// if touch is a drawing move
		if (touchPos != lastTouchPos) {
			currentCurve.add(touchPos.x, touchPos.y);

			// draw current curve to the canvas
			currentCurve.draw(pixmap);
			texture.draw(pixmap, 0, 0);
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		drawing.add(currentCurve);

		for (PeerID peerID : game.peers) {
			System.out.println(peerID.getIP().getHostAddress());
			try {
				// open socket
				Socket socket = new Socket(peerID.getIP(), peerID.getPort());

				// open streams
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());

				// send curve
				oos.writeObject(new Command(currentCurve));

				// close stream
				oos.close();

				// close socket
				socket.close();
			} catch (IOException e) {
				System.out.println("CanvasScreen.touchUp");
			}
		}

		currentCurve = new Curve();
		lastTouchPos = null;

		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.zoom += amount < 0 ? -0.1f : 0.1f;

		camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 2f * CANVAS_WIDTH
				/ viewportWidth);

		return true;
	}

	public boolean isRedrawing() {
		return redraw;
	}

}