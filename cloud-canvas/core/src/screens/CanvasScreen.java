package screens;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import launcher.CloudCanvas;
import peer.Listener;
import utils.Curve;

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

	public ArrayList<Curve> drawing;
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

		try {
			String tabletIP = "192.168.1.91";
			String pcIP = "192.168.1.128";

			// open socket
			Socket socket = new Socket(tabletIP, game.listenerPort);

			// open streams
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());

			// send curve
			oos.writeObject(currentCurve);

			// close stream
			oos.close();

			// close socket
			socket.close();
		} catch (IOException e) {
			System.out.println("the other peer is offline");
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

}