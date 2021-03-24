package me.datatags.commandminerewards;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public enum CMRPermission {
	HELP,
	RELOAD,
	MULTIPLIER,
	MULTIPLIER_MODIFY,
	BLOCK,
	BLOCK_MODIFY,
	REWARD,
	REWARD_MODIFY,
	COMMAND,
	COMMAND_MODIFY,
	COMMAND_EXECUTE,
	WORLD,
	WORLD_MODIFY,
	REGION,
	REGION_MODIFY,
	SILKTOUCHPOLICY,
	SILKTOUCHPOLICY_MODIFY,
	SPECIAL,
	LIMIT,
	LIMIT_MODIFY,
	GUI;
	public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to do that!";
	private Permission perm;
	private CMRPermission() {
		this.perm = new Permission("cmr." + this.name().toLowerCase().replace('_', '.'));
	}
	private CMRPermission(String suffix) {
		this.perm = new Permission("cmr." + suffix);
	}
	public Permission getPermission() {
		return perm;
	}
	public boolean test(CommandSender sender) {
		return sender.hasPermission(perm);
	}
	// how to remember which is which:
	// test is like testing the thin ice carefully
	// attempt is just going and standing in the middle of it
	public boolean attempt(CommandSender sender) {
		boolean allowed = test(sender);
		if (!allowed) {
			sender.sendMessage(NO_PERMISSION);
		}
		return allowed;
	}
}
