package commands;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import peer.Peer;
import utils.Curve;
import utils.Utils;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private CommandType type;

	private String originIP;

	private Curve curve;
	private Peer peer;
	private ArrayList<Peer> peers;
	public ArrayList<Curve> drawing;

	public Command(CommandType type) {
		this.type = type;

		addIP();
	}

	public Command(Peer peer) {
		this.type = CommandType.JOIN;

		addIP();

		this.peer = peer;
	}

	public Command(ArrayList<Peer> peers) {
		this.type = CommandType.PEERS;

		addIP();

		this.peers = peers;
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

	public Peer getPeer() {
		return peer;
	}

	public ArrayList<Peer> getPeers() {
		return peers;
	}

	public ArrayList<Curve> getDrawing() {
		return drawing;
	}

}
