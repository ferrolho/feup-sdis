package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import peer.Peer;
import chunk.ChunkID;

public class FileManager {

	private static final String CHUNKS = "CHUNKS/";

	private static final String RESTORES = "RESTORES/";

	public static boolean fileExists(String name) {
		File file = new File(name);

		return file.exists() && file.isFile();
	}

	private static boolean folderExists(String name) {
		File file = new File(name);

		return file.exists() && file.isDirectory();
	}

	private static void createFolder(String name) {
		File file = new File(name);

		file.mkdir();
	}

	public static final byte[] loadFile(File file) throws FileNotFoundException {
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

	public static final void saveChunk(ChunkID chunkID, byte[] data)
			throws IOException {
		if (!folderExists(CHUNKS))
			createFolder(CHUNKS);

		// write chunk
		FileOutputStream out = new FileOutputStream(CHUNKS + chunkID.toString());
		out.write(data);
		out.close();

		// update database
		Peer.getChunkDB().addChunk(chunkID);
		Peer.saveChunkDB();
	}

	public static final byte[] loadChunk(ChunkID chunkID)
			throws FileNotFoundException {
		File file = new File(CHUNKS + chunkID);
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

	public static final void saveRestore(String fileName, byte[] data)
			throws IOException {
		if (!folderExists(RESTORES))
			createFolder(RESTORES);

		FileOutputStream out = new FileOutputStream(RESTORES + fileName);
		out.write(data);
		out.close();
	}

}
