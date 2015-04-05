package database;

import java.io.Serializable;
import java.util.ArrayList;

import peer.PeerID;

public class ChunkInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int replicationDegree;
	private ArrayList<PeerID> mirrors;

	public ChunkInfo(int replicationDegree, ArrayList<PeerID> mirrors) {
		this.replicationDegree = replicationDegree;
		this.mirrors = mirrors;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public ArrayList<PeerID> getMirrors() {
		return mirrors;
	}

}
