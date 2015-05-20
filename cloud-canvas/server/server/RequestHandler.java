package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		System.out.println(t.getRequestURI().toString());
		InputStream is = t.getRequestBody();
		byte[] b = new byte[256];
		is.read(b);
		String response = "Ola";
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

	}

}
