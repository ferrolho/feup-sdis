package launcher;

import java.util.ArrayList;

import peer.PeerID;
import screens.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CloudCanvas extends Game {

	public ShapeRenderer shapeRenderer;
	public SpriteBatch spriteBatch;
	public BitmapFont font;

	public ArrayList<PeerID> peers;
	public int listenerPort;

	public void create() {
		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		peers = new ArrayList<PeerID>();
		listenerPort = 8008;

		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}

	public void dispose() {
		spriteBatch.dispose();
		font.dispose();
	}

}
