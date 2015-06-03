package commands;

import java.io.Serializable;
import java.util.ArrayList;

import peer.PeerID;
import utils.Curve;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private CommandType type;

	private Curve curve;
	private PeerID peerID;
	private ArrayList<PeerID> peers;
	public ArrayList<Curve> drawing;

	public Command(CommandType type) {
		this.type = type;
	}

	public Command(ArrayList<PeerID> peers) {
		this.type = CommandType.PEERS;
		this.peers = peers;
	}

	public CommandType getType() {
		return type;
	}

	public Curve getCurve() {
		return curve;
	}

	public PeerID getPeer() {
		return peerID;
	}

	public ArrayList<PeerID> getPeers() {
		return peers;
	}

	public ArrayList<Curve> getDrawing() {
		return drawing;
	}

}
