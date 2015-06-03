package commands;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import peer.PeerID;
import utils.Curve;
import utils.Utils;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private CommandType type;

	private String originIP;

	private Curve curve;
	private PeerID peerID;
	private ArrayList<PeerID> peers;
	public ArrayList<Curve> drawing;

	public Command(CommandType type) {
		this.type = type;

		try {
			originIP = Utils.getIPv4().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Command(ArrayList<PeerID> peers) {
		this.type = CommandType.PEERS;
		this.peers = peers;

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
