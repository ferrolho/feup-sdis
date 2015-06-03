package handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import server.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler {

	public Handler() {
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String path = t.getRequestURI().getPath();
		String[] paths = path.split("/");// paths[0] é vazio , paths[1] é sempre
											// canvas senão não chega a esta
											// função
		switch (paths[2]) {
		case "getRoomList":
			handleRoomList(t);
			break;
		case "joinRoom":
			handleJoinRoom(t);
			break;
		case "leaveRoom":
			handleLeaveRoom(t);

		default:
			sendResponse(t, 404, "nothing at all");
			break;
		}

	}

	private void handleRoomList(HttpExchange t) throws IOException {

		sendResponse(t, 200, Server.getRoomList());
	}

	private void handleJoinRoom(HttpExchange t) throws IOException {
		String[] query = readPostQuery(t).split("&");
		String[] roomQ = query[0].split("=");
		System.out.println(query[1]);
		if (roomQ[0].equals("roomName")) {
			String roomName = roomQ[1];
			System.out.println("Room Name is " + roomName);
			if (Server.roomExists(roomName)) {
				Server.incUsersRoom(roomName);
				sendResponse(t, 200, "Room sucefully joined");
			} else {
				sendResponse(t, 400, "Invalid roomName argument");

			}
			return;
		}
		sendResponse(t, 400, "Invalid query arguments");

	}

	private void handleLeaveRoom(HttpExchange t) throws IOException {
		
		String[] query = readPostQuery(t).split("&");
		System.out.println(query[1]);
		String[] roomQ = query[0].split("=");
		if (roomQ[0].equals("roomName")) {
			String roomName = roomQ[1];
			System.out.println("Room Name is " + roomName);
			if (Server.roomExists(roomName)) {
				Server.decUsersRoom(roomName);
				sendResponse(t, 200, "Room sucefully abandoned");
			} else {
				sendResponse(t, 400, "Invalid roomName argument");
			}
			return;
		}
		sendResponse(t, 400, "Invalid query arguments");

	}

	private void sendResponse(HttpExchange t, int statusCode, String response)
			throws IOException {
		t.sendResponseHeaders(statusCode, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private String readPostQuery(HttpExchange t) throws IOException {
		String qry;
		InputStream in = t.getRequestBody();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
			for (int n = in.read(buf); n > 0; n = in.read(buf)) {
				out.write(buf, 0, n);
			}
			qry = new String(out.toByteArray(), "ISO-8859-1");
		} finally {
			in.close();
		}
		return qry;
	}

}
