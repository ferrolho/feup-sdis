package utils;

import java.io.File;

import peer.Peer;

public class FileUtils {

	public static final String getFileID(File file) {
		String str = file.getAbsolutePath() + file.lastModified()
				+ Peer.getId();

		return Utils.sha256(str);
	}

}
