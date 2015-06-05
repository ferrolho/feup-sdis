package screens;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import launcher.CloudCanvas;
import peer.Forwarder;
import peer.NewPeerListener;
import peer.Peer;
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

public class CanvasScreen implements Screen, InputProcessor {

	private int CANVAS_WIDTH = 600;
	private int CANVAS_HEIGHT = 400;

	public final CloudCanvas game;

	private int viewportWidth, viewportHeight;
	private OrthographicCamera camera;

	public Pixmap pixmap;
	public Texture texture;

	public ArrayList<Curve> drawing;
	public Object drawingLock;
	private boolean redraw;

	private Curve currentCurve;
	private Vector3 lastTouchPos, touchPos;

	boolean ctrlIsBeingPressed = false;

	public ArrayList<Peer> peers;
	public int listenerPort;
	public Forwarder forwarder;

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
		drawingLock = new Object();
		redraw = false;

		currentCurve = new Curve();
		touchPos = new Vector3();

		camera = new OrthographicCamera();
		positionCamera();

		initPeerNetwork();
	}

	private void positionCamera() {
		camera.setToOrtho(false, viewportWidth, viewportHeight);
		camera.position.set(texture.getWidth() / 2, texture.getHeight() / 2, 0);
	}

	private void initPeerNetwork() {
		peers = new ArrayList<Peer>();

		listenerPort = 8008;

		forwarder = new Forwarder(this);

		new Thread(new NewPeerListener(this)).start();

		// access server information
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

				forwarder.sendJOIN(ip);
				forwarder.sendGET_PEERS();
				forwarder.sendPULL_DRAWING();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.err
					.println("CanvasScreen.initPeerNetwork - MalformedURLException");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("CanvasScreen.initPeerNetwork - IOException");
		}
	}

	private void createRoom() throws IOException, MalformedURLException {
		Utils.log("Creating new room.");

		String[] paramName = { "userIp" };
		String[] paramVal = { Utils.getIPv4().getHostAddress() };

		String ret = new HTTPRequest("/canvas/createRoom").POST(paramName,
				paramVal);

		Utils.log(ret);
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
			synchronized (drawingLock) {
				for (Curve curve : drawing)
					curve.draw(pixmap);
			}

			texture.draw(pixmap, 0, 0);

			redraw = false;
		}

		// draw the canvas with the drawing
		game.spriteBatch.begin();
		game.spriteBatch.draw(texture, 0, 0);
		game.spriteBatch.end();
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
		pixmap.dispose();
		texture.dispose();
		
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
		for (Peer peer : peers) {
			if (peer.socketIsSet()) {
				try {
					peer.getSocket().close();
				} catch (IOException e) {
					System.out.println("failed to close socket at end");
				}
			}
		}
	}

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
		updateCurrentCurve(screenX, screenY);

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		updateCurrentCurve(screenX, screenY);

		return true;
	}

	private void updateCurrentCurve(int screenX, int screenY) {
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
		currentCurve.finish();
		drawing.add(currentCurve);

		for (Peer peer : peers) {
			Utils.log("Sending CURVE to " + peer.getIP());

			forwarder.sendCURVE(currentCurve, peer);
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