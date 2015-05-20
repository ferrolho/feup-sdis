package com.feup.sdis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CanvasInputProcessor implements InputProcessor {

	private CanvasScreen screen;

	public CanvasInputProcessor(CanvasScreen canvasScreen) {
		this.screen = canvasScreen;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

	

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		screen.camera.zoom += amount < 0 ? -0.1f : 0.1f;

		screen.camera.zoom = MathUtils.clamp(screen.camera.zoom, 0.1f, 2f
				* screen.CANVAS_WIDTH / screen.viewportWidth);

		return true;
	}

}
