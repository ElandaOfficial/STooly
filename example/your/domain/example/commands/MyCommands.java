package your.domain.example.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.elanda.stooly.command.CommandProcessor;
import xyz.elanda.stooly.command.ICommand;
import xyz.elanda.stooly.command.ICommandTabImpl;
import xyz.elanda.stooly.permission.Permission;

public class MyCommands extends JavaPlugin
{
	private CommandProcessor commandTest = null;
	public static Permission permissionCommand = null;
	public static Permission permissionAdmin   = null;
	public static Permission permissionUser    = null;

	static
	{
		// permission example
		permissionCommand = new Permission("example.cmd");
		permissionAdmin   = permissionCommand.createSubPermission("admin"); // this is now example.cmd.admin
		permissionUser    = permissionCommand.createSubPermission("user"); // this is now example.cmd.user
	}
	
	public MyCommands()
	{
		// command example
		{
			// initializes the new command and automatically adds it to the command map
			commandTest = CommandProcessor.getInstance("test", "", "alias1", "alias2", "ect.");
			
			// some settings for the command
			commandTest.setCommandNotFoundMessage("Command {0} couldn't be found!");
			commandTest.setDescription("Just an example command!");
			commandTest.setUsage("Usage: /test <this | that>");
			commandTest.setPermissionMessage("You don't have the permissions to use this command!");
		}
	}
	
	final void registerSubCommands()
	{
		// register the command this
		commandTest.registerCommand(new CommandThis());
	}
	
	/*
	 * just a convenience getter to get the command
	 */
	final CommandProcessor getTestCommand()
	{
		return commandTest;
	}
	
	
	/*
	 * A sub command example
	 */
	class CommandThis implements ICommand, ICommandTabImpl
	{				
		public boolean execute(CommandSender sender, String[] args)
		{
			sender.sendMessage("Hello, this is the This command!");
			return true;
		}

		public String getDescription()
		{
			return "Prints out a message.";
		}

		public Permission getPermission()
		{
			return MyCommands.permissionAdmin.createSubPermission("this");
		}

		public String getUsage()
		{
			return "test this";
		}

		public String getName()
		{
			return "this";
		}

		public List<String> tabComplete(CommandSender sender, String[] args)
		{
			List<String> stringList = new ArrayList<String>();
			
			stringList.add("is");
			stringList.add("has");
			stringList.add("will");	
			
			return stringList;
		}
	}
}
