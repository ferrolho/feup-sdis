package initiators;

import java.io.File;

public class DeleteInitiator implements Runnable {

	private File file;

	public DeleteInitiator(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
