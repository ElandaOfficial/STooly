package xyz.elanda.stooly.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.md_5.bungee.api.ChatColor;
import xyz.elanda.stooly.permission.Permission;

/**
	The CommandProcessor class wraps functions for easy to handle command implementations.
	It takes a string which acts as command interpreter and handles this command with sub
	commands implementing the ICommand interface.
 */
public final class CommandProcessor extends Command
{
	
	private Map<String, ICommand> commands;
	
	private String noCmdMsg;
	
	/**
		Creates a new command.
		
		Note: You shouldn't use this to create new commands as adding them to the command
		      map is more inconvenient.
			  Use CommandProcessor.getInstance() instead to let the class manage this for
			  you.
		
		@param commandName The name of the main command
     */
	public CommandProcessor(String commandName)
	{
		super(commandName);
		commands = Maps.newHashMap();
		noCmdMsg = "Couldn't find command {0}, use '/" + commandName + " help' to get a list of possible commands!";
	}
	
	/**
		This is the execution routine of any command and splits it into tinier commands.
		A command as "/command subcomman ect." will be split into a new array of "subcomman ect."
		and will read if any subcommands with the name "subcommand" exist and execute it.
		
		@param sender       The command sender
		@param commandLabel The label of the command, means that if an alias was used, this will be
		                    the alias
		@param args	        An array containing the command keys
		@return True if the command was executed successfully, false if not
	 */
	public final boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("help"))
			{
				if(commands.size() > 0 && sender instanceof Player)
				{
					final Player player = (Player)sender;
					final int maxPerPage = 8;
					int maxPages = (int)(commands.size() / maxPerPage);
					int currentPage = 0;
					
					if(maxPages == 0 && commands.size() > 0)
					{
						maxPages = 1;
					}
					
					if(args.length > 1)
					{
						try
						{
							currentPage = Integer.parseInt(args[1]);
						}
						catch(NumberFormatException ex)
						{
							player.sendMessage(getUsage());
						}
					}	
					
					if(currentPage * maxPerPage >= commands.size())
					{
						currentPage = maxPages - 1;
					}
					else if (currentPage < 0)
					{
						currentPage = 0;
					}
					
					int startIndex = currentPage * maxPerPage;
					
					if(startIndex >= commands.size())
					{
						startIndex = commands.size() - maxPerPage;
					}
					
					if(startIndex < 0)
					{
						startIndex = 0;
					}
					
					player.sendMessage(String.format(ChatColor.translateAlternateColorCodes
									  ('&', "&a        ReverseFortnite Help (Page %d/%d)"),
									   currentPage + 1, maxPages));
					player.sendMessage(ChatColor.translateAlternateColorCodes
					                  ('&', "&a============================================"));
					ICommand[] cmds = Arrays.copyOfRange(commands.values().toArray(new ICommand[0]), startIndex,
													     startIndex + maxPerPage >= commands.size() ? startIndex
							                            + (commands.size() - startIndex) : startIndex + maxPerPage);
					Arrays.sort(cmds, Comparator.comparing(ICommand::getName));
					
					for(int i = 0; i < cmds.length; ++i)
					{
						ICommand cmd = cmds[i];
						
						if(cmd.getPermission().playerHasPermission(player))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes
											  ('&', "&a" + cmd.getName().toLowerCase() +
												    "&f: " +cmd.getDescription()));
						}
					}
						
					player.sendMessage(ChatColor.translateAlternateColorCodes
									  ('&', "&a============================================"));

					return true;
				}
			}
			else if(commands.containsKey(args[0].toLowerCase()))
			{
				ICommand cmd = commands.get(args[0].toLowerCase());
				Permission perm = cmd.getPermission();
				
				if(!(sender instanceof Player) ||
				    (perm == null || perm.playerHasPermissionAirLeftUpon((Player)sender)))
				{
					return cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
				}
				
				sender.sendMessage(getPermissionMessage());
				return false;
			}
			
			sender.sendMessage(noCmdMsg.replace("{0}", args[0]));
			return false;
		}
		
		sender.sendMessage(getUsage());
		return false;
	}
	
	/**
		Sets the message if a sub-command wasn't found.
		
		@param message The message
	 */
	public final void setCommandNotFoundMessage(String message)
	{
		noCmdMsg = message;
	}
	
	/**
		Gets the current set message for sub-commands which weren't found.
		
		@return The message
	 */
	public final String getCommandNotFoundMessage()
	{
		return noCmdMsg;
	}
	
	/**
		Registers a new sub command for this command.
		
		@param command The sub command to register for this command
	 */
	public void registerCommand(ICommand command)
	{	
		if(!commands.containsKey(command.getName().toLowerCase()))
		{
			commands.put(command.getName().toLowerCase(), command);
		}
	}
	
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
		   throws IllegalArgumentException
	{
		if(args.length == 0)
		{
			return commands.size() > 0 ? Lists.newArrayList(commands.keySet()) :
			                             super.tabComplete(sender, alias, args);
		}
		
		if(args.length >= 1)
		{
			String command = args[0];
			
			if(commands.containsKey(command) && commands.get(command) instanceof ICommandTabImpl)
			{
				ICommandTabImpl commandImpl = (ICommandTabImpl) commands.get(command);
				
				if(commandImpl!=null)
				{
					List<String> result = commandImpl.tabComplete(sender,
					                      Arrays.copyOfRange(args, 1, args.length - 1));
					return result != null ? result : super.tabComplete(sender, alias, args);
				}
			}
		}
		return super.tabComplete(sender, alias, args);
	}
	
	public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
		   throws IllegalArgumentException
	{
		if(args.length <= 1)
		{
			return commands.size() > 0 ? Lists.newArrayList(commands.keySet()) :
			                             super.tabComplete(sender, alias, args, location);
		}
		
		if(args.length > 1)
		{
			String command = args[0];
			
			if(commands.containsKey(command) && commands.get(command) instanceof ICommandTabImpl)
			{
				ICommandTabImpl commandImpl = (ICommandTabImpl) commands.get(command);
				
				if(commandImpl!=null)
				{
					List<String> result = commandImpl.tabComplete(sender,
										  Arrays.copyOfRange(args, 1, args.length - 1), location);
					return result != null ? result :
					                 super.tabComplete(sender, alias, args, location);
				}
			}
		}
		
		return super.tabComplete(sender, alias, args, location);
	}
	
	/**
		Creates a new core command and adds it to the server command map.
		
		@param name 	   The name of the core command
		@param description The description of the command
		@param aliases	   An array of aliases which will also invoke this command
		@return The newly created command if it didn't exist already
	 */
	public static CommandProcessor getInstance(String name, String description, String...aliases)
	{
		try
		{
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            
            CommandProcessor cp = new CommandProcessor(name.toLowerCase());
            cp.setDescription(description);
            cp.setAliases(Lists.newArrayList(aliases));
            
            if(commandMap.getCommand(name.toLowerCase())==null)
			{
            	commandMap.register(name.toLowerCase(), "cpcp", cp);
            }
			else
			{
            	Command cmd = commandMap.getCommand(name.toLowerCase());
				
            	if(cmd instanceof CommandProcessor)
				{
            		return (CommandProcessor)commandMap.getCommand(name.toLowerCase());
            	}
				else
				{
            		return null;
				}
			}
            	
            return cp;
        }
		catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex)
		{
			throw new RuntimeException("Command '" + name.toLowerCase() + "' couldn't be registered! Is the plugin up to date?");
        }
	}
}
