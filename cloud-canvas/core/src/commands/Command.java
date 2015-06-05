package commands;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import utils.Curve;
import utils.Utils;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private CommandType type;

	private String originIP;

	private Curve curve;
	private ArrayList<String> peersIP;
	public ArrayList<Curve> drawing;

	public Command(CommandType type) {
		this.type = type;

		addIP();
	}

	public Command(ArrayList<String> peersIP) {
		this.type = CommandType.PEERS;

		addIP();

		this.peersIP = peersIP;
	}

	public Command(ArrayList<Curve> drawing, int dummyNo) {
		this.type = CommandType.DRAWING;

		addIP();

		this.drawing = drawing;
	}

	public Command(Curve curve) {
		this.type = CommandType.CURVE;

		addIP();

		this.curve = curve;
	}

	private void addIP() {
		try {
			originIP = Utils.getIPv4().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CommandType getType() {
		return type;
	}

	public String getOriginIP() {
		return originIP;
	}

	public Curve getCurve() {
		return curve;
	}

	public ArrayList<String> getPeersIP() {
		return peersIP;
	}

	public ArrayList<Curve> getDrawing() {
		return drawing;
	}

}
