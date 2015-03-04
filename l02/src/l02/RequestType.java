package l02;

public enum RequestType {
	REGISTER("register"), LOOKUP("lookup");

	private final String text;

	/**
	 * @param text
	 */
	private RequestType(String text) {
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
