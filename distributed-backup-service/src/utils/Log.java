package utils;

public class Log {

	public static final void error(String msg) {
		System.err.println();
		System.err.println("ERROR: " + msg);
		System.err.println();
	}

	public static final void info(String msg) {
		System.out.println("INFO: " + msg);
	}

}
