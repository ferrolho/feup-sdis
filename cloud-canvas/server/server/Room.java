package server;

import java.net.InetAddress;


public class Room {

	private InetAddress ip;
	private int numUsers;

	public Room(InetAddress ip) {
		this.ip = ip;
		this.numUsers = 0;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getNumUsers() {
		return numUsers;
	}

	public boolean isEmpty() {
		return numUsers == 0;
	}

	public void incNumUsers() {
		numUsers++;
		System.out.println("room updated");
	}

	public void decNumUsers() {
		numUsers--;
	}

}
