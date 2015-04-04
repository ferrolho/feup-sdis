package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import peer.Peer;

public class FileUtils {

	public static boolean fileExists(String fileName) {
		File file = new File(fileName);

		return file.exists() && file.isFile();
	}

	public static final String getFileID(File file) {
		String str = file.getAbsolutePath() + file.lastModified()
				+ Peer.getId().getIP();

		return Utils.sha256(str);
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

}
