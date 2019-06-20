package xyz.elanda.stooly.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
	This interface is a simple implementation for custom tab implementation displays.
	Every ICommand which needs tab complete suggestions, should implement this interface.
 */
public interface ICommandTabImpl
{
	List<String> tabComplete(CommandSender sender, String[] args);
	
	default List<String> tabComplete(CommandSender sender, String[] args, Location location)
	{
		return tabComplete(sender, args);
	}
}
