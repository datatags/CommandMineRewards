package me.datatags.commandminerewards;

import org.bukkit.ChatColor;

public enum SilkTouchPolicy {
	REQUIRED(ChatColor.GREEN + "Required"),
	IGNORED(ChatColor.YELLOW + "Ignored"),
	DISALLOWED(ChatColor.RED + "Disallowed"),
	INHERIT(ChatColor.GRAY + "Inherited"),
	;
	
	private String friendlyName;
	private SilkTouchPolicy(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	public static SilkTouchPolicy getByName(String name) {
		for (SilkTouchPolicy val : SilkTouchPolicy.values()) {
			if (val.toString().equalsIgnoreCase(name)) {
				return val;
			}
		}
		return null;
	}
	public String getFriendlyName() {
		return this.friendlyName;
	}
}
