package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Peer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ip;
	private int port;

	private Socket socket;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;

	public Peer(String ip) throws IOException {
		this.ip = ip;
		this.port = 8008;

		socket = new Socket(ip, port);
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
	}

	public String getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void setNetworkData(Socket socket, ObjectInputStream ois,
			ObjectOutputStream oos) {
		this.socket = socket;
		this.ois = ois;
		this.oos = oos;
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean socketIsSet() {
		return socket != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Peer other = (Peer) obj;

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
