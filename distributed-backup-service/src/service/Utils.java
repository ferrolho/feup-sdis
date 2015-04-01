package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Random;

public class Utils {

	public static Random random = new Random();

	public static final void printError(String msg) {
		System.err.println();
		System.err.println("ERROR: " + msg);
		System.err.println();
	}

	public static byte[] concatBytes(byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;

		byte[] c = new byte[aLen + bLen];

		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static InetAddress getIPv4() {
		System.setProperty("java.net.preferIPv4Stack", "true");

		InetAddress ip = null;

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();

				while (addresses.hasMoreElements())
					ip = addresses.nextElement();
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		return ip;
	}

	public static final String getFileID(File file) {
		String str = file.getAbsolutePath() + file.lastModified()
				+ getFileOwner(file);

		return sha256(str);
	}

	private static final String getFileOwner(File file) {
		String owner = "";

		try {
			Path path = Paths.get(file.getName());

			FileOwnerAttributeView view = Files.getFileAttributeView(path,
					FileOwnerAttributeView.class);

			owner = view.getOwner().getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return owner;
	}

	public static final byte[] getFileData(File file)
			throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(file);

		byte[] data = new byte[(int) file.length()];

		try {
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	private static final String sha256(String str) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");

			byte[] hash = sha.digest(str.getBytes(StandardCharsets.UTF_8));

			StringBuffer hexStringBuffer = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);

				if (hex.length() == 1)
					hexStringBuffer.append('0');

				hexStringBuffer.append(hex);
			}

			return hexStringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
