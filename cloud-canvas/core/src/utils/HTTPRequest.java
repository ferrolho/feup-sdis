package utils;

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

import utils.Utils;

public class HTTPRequest {

	private URL url;

	public HTTPRequest(String file) throws MalformedURLException {
		this.url = new URL("http", "46.101.171.164", 8000, file);
	}

	public String GET(String charset) throws IOException {
		// Starting HTTP connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// request headers
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept-Charset", charset);
		conn.setRequestProperty("Content-Type", "" + Utils.CONTENT_TYPE
				+ charset);
		conn.setRequestProperty("User-Agent", Utils.USER_AGENT);

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

		if (conn != null)
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

		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");

		// Create the form content
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		for (int i = 0; i < paramName.length; i++) {
			writer.write(paramName[i]);
			writer.write("=");
			writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			if (i != paramName.length - 1) {
				writer.write("&");
			}
		}
		writer.close();
		out.close();

		// check connection response
		if (conn.getResponseCode() != 200) {
			System.out.println("Response code: " + conn.getResponseCode());
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

		if (conn != null)
			conn.disconnect();

		return sb.toString();
	}

}
