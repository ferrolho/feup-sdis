package utils;

import java.io.File;

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

}
