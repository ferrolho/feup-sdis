package service;

public class Commands {

	public static final String BACKUP = "backup";
	public static final String BACKUP_USAGE = Commands.BACKUP
			+ " <file> <replication degree>";
	public static final int BACKUP_NUM_ARGS = 3;

	public static final String DELETE = "delete";
	public static final String DELETE_USAGE = Commands.DELETE + " <file>";
	public static final int DELETE_NUM_ARGS = 2;

	public static final String FREE = "free";
	public static final String FREE_USAGE = Commands.FREE + " <kbyte>";
	public static final int FREE_NUM_ARGS = 2;

	public static final String RESTORE = "restore";
	public static final String RESTORE_USAGE = Commands.RESTORE + " <file>";
	public static final int RESTORE_NUM_ARGS = 2;

	public static final void printUsage() {
		System.out.println("Usage:");
		System.out.println("\t" + BACKUP_USAGE);
		System.out.println("\t" + DELETE_USAGE);
		System.out.println("\t" + FREE_USAGE);
		System.out.println("\t" + RESTORE_USAGE);
	}
}
