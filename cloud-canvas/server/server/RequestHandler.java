package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler {
	
	public RequestHandler(){
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String path = t.getRequestURI().getPath();
		String[] paths = path.split("/");
		for (String string : paths) {
			System.out.println(string);
		}
		if(paths[1].equals("canvas")){
			if(paths[2].equals("getRoomList") && paths.length ==3){
				InputStream is = t.getRequestBody();
				byte[] b = new byte[256];
				is.read(b);
				String response = Main.getRoomList();
				System.out.println(response);
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
				return;
			}
			else if(paths[2].equals("joinRoom") && paths.length==3){
				InputStream is = t.getRequestBody();
				byte[] b = new byte[256];
				is.read(b);
				String qr = t.getRequestURI().getQuery();
				System.out.println(qr);
				String[] query = qr.split("=");
				System.out.println(query.length);
				if(query[0].equals("name") && query.length == 2){
					Main.incUsers(query[1]);
					System.out.println(query[1]);
					String response= "added";
					t.sendResponseHeaders(200, response.length());
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
					return;
				}
				
			}
			else if(paths[2].equals("leaveRoom") && paths.length==3){
				InputStream is = t.getRequestBody();
				byte[] b = new byte[256];
				is.read(b);
				String qr = t.getRequestURI().getQuery();
				System.out.println(qr);
				String[] query = qr.split("=");
				System.out.println(query.length);
				if(query[0].equals("name") && query.length == 2){
					Main.decUsers(query[1]);
					System.out.println(query[1]);
					String response= "left";
					t.sendResponseHeaders(200, response.length());
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
					return;
				}
				
			}
		}
		InputStream is = t.getRequestBody();
		byte[] b = new byte[256];
		is.read(b);
		String response = "Error: missing\n";
		t.sendResponseHeaders(404, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

		
	}

}
