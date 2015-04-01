package trigger;

public class TriggerCommands {

	public static final String BACKUP = "backup";
	public static final String RESTORE = "restore";
	public static final String DELETE = "delete";
	public static final String SPACE = "space";

	public static final int BACKUP_NUM_ARGS = 4;
	public static final int RESTORE_NUM_ARGS = 3;
	public static final int DELETE_NUM_ARGS = 3;
	public static final int SPACE_NUM_ARGS = 3;

	public static final String BACKUP_USAGE = "<RMI obj. name> "
			+ TriggerCommands.BACKUP + " <file path> <replication degree>";

	public static final String RESTORE_USAGE = "<RMI obj. name> "
			+ TriggerCommands.RESTORE + " <file path>";

	public static final String DELETE_USAGE = "<RMI obj. name> "
			+ TriggerCommands.DELETE + " <file path>";

	public static final String SPACE_USAGE = "<RMI obj. name> "
			+ TriggerCommands.SPACE + " <amount of space>";

	public static final void printUsage(String className) {
		System.out.println("Usage:");
		System.out.println("\tjava " + className + " " + BACKUP_USAGE);
		System.out.println("\tjava " + className + " " + RESTORE_USAGE);
		System.out.println("\tjava " + className + " " + DELETE_USAGE);
		System.out.println("\tjava " + className + " " + SPACE_USAGE);
	}

}
