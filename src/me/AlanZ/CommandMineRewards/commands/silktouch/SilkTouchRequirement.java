package me.AlanZ.CommandMineRewards.commands.silktouch;

import org.bukkit.ChatColor;

public enum SilkTouchRequirement {
	REQUIRED,
	IGNORED,
	DISALLOWED;
	
	public static SilkTouchRequirement getByName(String name) {
		for (SilkTouchRequirement val : SilkTouchRequirement.values()) {
			if (val.toString().equalsIgnoreCase(name)) {
				return val;
			}
		}
		return null;
	}
	public String getFriendlyName() {
		if (this == SilkTouchRequirement.REQUIRED) {
			return ChatColor.GREEN + "Required";
		} else if (this == SilkTouchRequirement.IGNORED) {
			return ChatColor.YELLOW + "Ignored";
		} else if (this == SilkTouchRequirement.DISALLOWED) {
			return ChatColor.RED + "Disallowed";
		} else {
			return null;
		}
	}
}
