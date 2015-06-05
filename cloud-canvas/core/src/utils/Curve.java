package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Curve implements Serializable {

	private static final long serialVersionUID = 1L;

	ArrayList<Vector2> vertices;
	Vector3 color;

	Date date;

	public Curve() {
		vertices = new ArrayList<Vector2>();

		color = new Vector3(MathUtils.random(), MathUtils.random(),
				MathUtils.random());
	}

	public void add(float x, float y) {
		vertices.add(new Vector2(x, y));
	}

	public void draw(Pixmap pixmap) {
		pixmap.setColor(color.x, color.y, color.z, 1);

		for (int i = 0, j = 1; j < vertices.size(); i++, j++)
			pixmap.drawLine((int) vertices.get(i).x, (int) vertices.get(i).y,
					(int) vertices.get(j).x, (int) vertices.get(j).y);
	}

	public void finish() {
		date = new Date();
	}

	public Date getDate() {
		return date;
	}

}
