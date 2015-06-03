package peer;

import java.io.Serializable;
import java.net.InetAddress;

public class PeerID implements Serializable {

	private static final long serialVersionUID = 1L;

	private InetAddress ip;
	private int port;

	public PeerID(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public InetAddress getIP() {
		return ip;
	}

	public int getPort() {
		return port;
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

	@Override
	public String toString() {
		return ip + ":" + port;
	}

}
