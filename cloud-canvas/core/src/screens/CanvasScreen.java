package screens;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;

import launcher.CloudCanvas;
import peer.Listener;
import peer.PeerID;
import utils.Curve;
import utils.HttpRequest;
import utils.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

	// TODO change this
	private int CANVAS_WIDTH = 400;
	private int CANVAS_HEIGHT = 400;

	public final CloudCanvas game;

	private OrthographicCamera camera;

	private Texture dropImage;
	private Texture bucketImage;

	private Sound dropSound;
	private Music rainMusic;

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

		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setVolume(0.5f);
		rainMusic.setLooping(true);

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

		{
			try {
				HttpRequest request = new HttpRequest("/canvas/getRoomList");
				String roomsStr = request.GET(Utils.UTF_8);

				if (roomsStr.isEmpty()) {
					System.out.println("no rooms! creating a new one");

					String[] paramName = { "userIp" };
					String[] paramVal = { Utils.getIPv4().getHostAddress() };

					String ret = new HttpRequest("/canvas/createRoom").POST(
							paramName, paramVal);

					System.out.println("RET: " + ret);
				} else {
					String[] rooms = roomsStr.split("\\s+");
					System.out.println("Room: " + rooms[0]);

					String ip = rooms[0].split(",")[1];
					System.out.println("IP: -" + ip + "-");

					{
						// open socket
						Socket tempsocket = new Socket(ip, 8008);
						System.out.println("asking " + ip + " for peers");

						// open streams
						ObjectOutputStream oos = new ObjectOutputStream(
								tempsocket.getOutputStream());

						// send curve
						oos.writeObject(new Command(CommandType.GET_PEERS));

						ObjectInputStream ois = new ObjectInputStream(
								tempsocket.getInputStream());

						try {
							Command command = (Command) ois.readObject();
							System.out.println("TEST: " + command.getType());

							game.peers = command.getPeers();
							System.out.println("22Peers received: "
									+ game.peers);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

						// close stream
						oos.close();
						ois.close();

						// close socket
						tempsocket.close();
					}
				 //joins
					
				}
			} catch (MalformedURLException e) {
				System.out.println("damn you BOTAS!");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("damn you BOTAS 2!");
				e.printStackTrace();
			}
		}

		new Thread(new Listener(this)).start();

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
		rainMusic.play();
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
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		String[] paramName = { "roomName" };
		String[] paramVal = {"Sala" };
		try {
			new HttpRequest("/canvas/leaveRoom").POST(
					paramName, paramVal);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("merdou a sair da sala");
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

				redraw();
			}
			break;

		default:
			break;
		}

		return true;
	}

	public void redraw() {
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