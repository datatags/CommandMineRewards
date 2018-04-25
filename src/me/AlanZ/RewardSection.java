package me.AlanZ;

import org.bukkit.configuration.ConfigurationSection;

public class RewardSection {
	private ConfigurationSection section;
	static CommandMineRewards cmr = null;
	public RewardSection(String path) {
		if (cmr == null) {
			throw new IllegalStateException("CMR instance has not been set!");
		}
		if (!cmr.getConfig().isConfigurationSection(path)) {
			throw new RewardSectionInvalidException("Reward section " + path + " does not exist!");
		}
		section = cmr.getConfig().getConfigurationSection(path);
	}
	public RewardSection(ConfigurationSection section) {
		this.section = section;
	}
	
}
