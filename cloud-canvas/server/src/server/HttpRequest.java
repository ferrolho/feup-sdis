package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpRequest {
	private URL url;

	public HttpRequest(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public String GET() throws IOException {
		// Starting HTTP connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");

		// check connection response
		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	public String POST(String[] paramName, String[] paramVal)
			throws IOException {
		// Starting HTTP connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		// TODO put here the requests properties we have to create

		// Create the form content
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		for (int i = 0; i < paramName.length; i++) {
			writer.write(paramName[i]);
			writer.write("=");
			writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			writer.write("&");
		}
		writer.close();
		out.close();

		// check connection response
		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		return sb.toString();
	}

	public void PUT() throws IOException {
		// Starting HTTP connection
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

		httpCon.setRequestMethod("PUT");
		httpCon.setDoOutput(true);

		OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
		out.write("Resource content");
		out.close();

		// check connection response
		if (httpCon.getResponseCode() != 200) {
			throw new IOException(httpCon.getResponseMessage());
		}

		// TODO what we need to put in there

		httpCon.disconnect();
	}

	public void DELETE() throws IOException {
		// Starting HTTP connection
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

		httpCon.setRequestMethod("DELETE");
		httpCon.setDoOutput(true);
		// TODO put here the requests properties we have to create

		// check connection response
		if (httpCon.getResponseCode() != 200) {
			throw new IOException(httpCon.getResponseMessage());
		}

		// TODO what we need to put in there

		httpCon.disconnect();
	}
}
