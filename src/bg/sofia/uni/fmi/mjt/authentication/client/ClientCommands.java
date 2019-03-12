package bg.sofia.uni.fmi.mjt.authentication.client;

public enum ClientCommands {
	REGISTER("register"), LOGIN("login"), RESET_PASSWORD("reset-password"), UPDATE_USER("update-user"),
	LOGOUT("logout"), DELETE_USER("delete-user");

	private String commandText;

	ClientCommands(String text) {
		commandText = text;
	}

	public static ClientCommands findCommandByValue(String value) {
		for (ClientCommands command : values()) {
			if (command.commandText.equals(value)) {
				return command;
			}
		}
		return null;
	}

};
