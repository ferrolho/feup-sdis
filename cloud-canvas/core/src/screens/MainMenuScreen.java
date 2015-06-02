package screens;

import java.io.IOException;

import launcher.CloudCanvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class MainMenuScreen implements Screen {

	final CloudCanvas game;

	OrthographicCamera camera;

	private String titleStr = "Cloud Canvas";
	private Vector2 titlePos;

	private String infoStr = "Tap anywhere to draw";
	private Vector2 infoPos;

	public MainMenuScreen(final CloudCanvas game) {
		this.game = game;

		titlePos = new Vector2(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() * 0.7f);

		infoPos = new Vector2(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() * 0.5f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.6f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.spriteBatch.begin();

		// title
		game.font.draw(game.spriteBatch, titleStr, titlePos.x, titlePos.y, 0,
				Align.center, true);

		// info
		game.font.draw(game.spriteBatch, infoStr, infoPos.x, infoPos.y, 0,
				Align.center, true);

		game.spriteBatch.end();

		if (Gdx.input.isTouched()) {
			try {
				game.setScreen(new CanvasScreen(game));
				dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
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
	}

}
