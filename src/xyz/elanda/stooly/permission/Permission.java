package xyz.elanda.stooly.permission;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
	The permission class is a helper class for permission integration.
	You can simply name a permission and let this class handler everything automatically.
 */
public final class Permission
{
	private static final Map<String, Permission> PERM_CACHE;
	
	private final String name;
	private Permission superPermission;
	
	static
	{
		PERM_CACHE = new HashMap<String, Permission>();
	}
	
	/**
		Creates a new core permission with a given name.
		
		@param name The name of the permission
	 */
	public Permission(String name)
	{
		this.name = name;
		this.superPermission = null;
	}
	
	/**
		Creates a new sub permission with a given name and a given parent permission.
		
		@param name 		   The name of the permission
		@param superPermission The parent permission
	 */
	public Permission(String name, Permission superPermission)
	{
		this.name = superPermission.name + "." + name;
		this.superPermission = superPermission;
	}
	
	/**
		Checks whether a given player has this permission.
		
		@param player The player to check for
		@return True if the player has the permission, false if not
	 */
	public boolean playerHasPermission(Player player)
	{
		return player.hasPermission("*") || player.hasPermission(name) || (superPermission != null ?
				                            (player.hasPermission(superPermission.name + ".*") ||
				                            superPermission.playerHasPermission(player)) : false);
	}
	
	/**
		Checks whether a given player has this permission or has at least this permission
		with a lot of children permission.
		
		For example, this will return true if this permission is:
		- plugin.cmd.admin
		
		And the player has one of those permission:
		- plugin.cmd.admin.subperm
		- plugin.cmd.admin.otherperm
		- plugin.cmd.admin
		
		@param player The player to check for
		@return True if the player has such a permission, false if not
	 */
	public boolean playerHasPermissionAirLeftUpon(Player player)
	{
		if(playerHasPermission(player))
		{
			return true;
		}
		else
		{
			for(PermissionAttachmentInfo pat : player.getEffectivePermissions())
			{
				if(pat.getPermission().startsWith(name))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
		This will check if the player has a sub permission.
		
		@param player The player to check for
		@param name   The name of the sub permission chain
		@return True if the player has the permission, false if not
	 */
	public boolean playerHasPermission(Player player, String name)
	{
		String subName = this.name + "." + name;
		
		if(!PERM_CACHE.containsKey(subName))
		{
			Permission perm = createSubPermission(name);
			PERM_CACHE.put(subName, perm);
			return perm.playerHasPermission(player);
		}
		
		return PERM_CACHE.get(subName).playerHasPermission(player);
	}
	
	/**
		This will create a sub permission and return it.
		
		@param name The name of the sub permission
		@return The new permission
	 */
	public Permission createSubPermission(String name)
	{
		return new Permission(this.name + "." + name, this);
	}
}
