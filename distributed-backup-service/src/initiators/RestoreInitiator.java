package initiators;

import java.io.File;

public class RestoreInitiator implements Runnable {

	private File file;

	public RestoreInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {
	}

}
