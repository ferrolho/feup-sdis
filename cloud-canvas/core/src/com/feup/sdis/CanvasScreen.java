package com.feup.sdis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CanvasScreen implements Screen {

	// TODO change this
	int CANVAS_WIDTH = 400;
	int CANVAS_HEIGHT = 400;

	private final CloudCanvas game;

	OrthographicCamera camera;

	private Texture dropImage;
	private Texture bucketImage;

	private Sound dropSound;
	private Music rainMusic;

	Pixmap pixmap;
	Texture texture;

	boolean touching;
	Vector3 lastTouchPos, touchPos;

	int viewportWidth, viewportHeight;

	public CanvasScreen(final CloudCanvas game) {
		this.game = game;

		Gdx.input.setInputProcessor(new CanvasInputProcessor(this));

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

		touching = false;
		touchPos = new Vector3();

		camera = new OrthographicCamera();
		positionCamera();
	}

	private void positionCamera() {
		camera.setToOrtho(false, viewportWidth, viewportHeight);
		camera.position.set(texture.getWidth() / 2, texture.getHeight() / 2, 0);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.spriteBatch.setProjectionMatrix(camera.combined);

		game.spriteBatch.begin();
		game.spriteBatch.draw(texture, 0, 0);
		game.spriteBatch.end();

		if (touching && touchPos != lastTouchPos) {
			game.shapeRenderer.begin(ShapeType.Filled);

			pixmap.setColor(0, 0, 0, 1);
			pixmap.drawLine((int) lastTouchPos.x, (int) lastTouchPos.y,
					(int) touchPos.x, (int) touchPos.y);
			System.out.println("drawing from : " + lastTouchPos.x + " "
					+ lastTouchPos.y + " to " + touchPos.x + " " + touchPos.y);
			texture.draw(pixmap, 0, 0);

			game.shapeRenderer.end();
		}
		if (Gdx.input.isTouched()) {
			touching = true;
			if (lastTouchPos != null)
				lastTouchPos.set(touchPos);
			touchPos.set(Gdx.input.getX(),
					Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (lastTouchPos == null)
				lastTouchPos = new Vector3(touchPos);
		} else {
			touching = false;
			lastTouchPos = null;
		}

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

}
