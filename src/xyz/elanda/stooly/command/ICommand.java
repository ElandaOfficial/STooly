package xyz.elanda.stooly.command;

import org.bukkit.command.CommandSender;

import xyz.elanda.stooly.permission.Permission;

/**
	The ICommand interface is a simple wrapper interface to simplify the use and
	integration of commands in Spigot/Bukkit.
	
	Let your command inherit from this class and get your plugin going.
 */
public interface ICommand
{
	/**
		The execution command handles the process when the command was called.
		
		@param sender The command sender, may be a Player or a Block or ect.
		@param args   The argument list containing the further list of arguments
		@return True if the command was successfully executed, false if not
	 */
	public boolean execute(CommandSender sender, String[] args);
	
	/**
		Gets the description of the command for the help command.
		
		@return The description
	 */
	public String getDescription();
	
	/**
		Gets the permission node of this command.
		
		@return The permission
	 */
	public Permission getPermission();
	
	/**
		Gets the usage text of this command used for the help command.
		
		@return The usage text
	 */
	public String getUsage();
	
	/**
		Gets the name of the command.
		
		@return The name of the command
	 */
	public String getName();
}
