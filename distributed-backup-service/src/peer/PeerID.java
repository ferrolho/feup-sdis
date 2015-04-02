package peer;

import java.net.InetAddress;

public class PeerID {

	private InetAddress ip;
	private int port;

	public PeerID(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		PeerID other = (PeerID) obj;

		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;

		if (port != other.port)
			return false;

		return true;
	}

	public InetAddress getIP() {
		return ip;
	}

}
