package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.security.MessageDigest;

public class Utils {

	public static final void printError(String msg) {
		System.err.println();
		System.err.println("ERROR: " + msg);
		System.err.println();
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

	public static final String getFileData(File file) {
		String dataStr = "";

		try {
			byte[] data = new byte[(int) file.length()];

			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(data);
			inputStream.close();

			dataStr = new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataStr;
	}

	private static final String sha256(String str) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] hash = sha.digest(str.getBytes("UTF-8"));
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
