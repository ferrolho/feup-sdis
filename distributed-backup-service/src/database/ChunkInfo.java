package database;

import java.util.ArrayList;

import peer.PeerID;

public class ChunkInfo {

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
