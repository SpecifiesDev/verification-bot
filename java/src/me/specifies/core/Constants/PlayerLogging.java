package me.specifies.core.Constants;

public interface PlayerLogging {
	
	// Invalid permissions, or sender types
	public static final String INVALID_PERMISSIONS = "&cIt would appear that you do not have permissions to do this.";
	public static final String PLAYER_ONLY = "&cYou must be a player to use this command.";
	
	// Account linking through /link
	public static final String VERIFIED = "&aYou have already verified your account to this server's discord.";
	public static final String PENDING = "&aYour account is already pending verification. You may verify it in the discord server using the code: &6";
	public static final String CREATED_CODE = "&aYour account is now pending verification. You may now verify in the discord server using the code: &6";
	
	// Error logging to players
	public static final String INTERNAL_ERROR = "&cAn internal error occured. Please contact an Administrator.";
	public static final String COMMAND_TIMEOUT = "&cThis command's functionality has timed out. Please try again.";
	public static final String LOCAL_INTERNAL_ERROR = "&cThere was an internal error. Please contact an Administrator.";
	
	// Linking through strict verification mode
	public static final String CREATED_CODE_STRICT = "&aYou must be verified to join this server. You may join our discord and verify with the code: &6\n";
	public static final String PENDING_STRICT =  "&aYou must complete the verification process on our discord to join. Your code: &6\n";
	
	// Preferences logging
	public static final String PREF_NO_LINK = "&cSorry, in order to use this command you must be linked to our discord.";
	public static final String PREF_UPDATED = "&aYour preferences have been updated.";
	
}
