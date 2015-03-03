package l01;

public enum MessageType {
	REGISTER("register"), LOOKUP("lookup");

	private final String text;

	/**
	 * @param text
	 */
	private MessageType(String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
}
