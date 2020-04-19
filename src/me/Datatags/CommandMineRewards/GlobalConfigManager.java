package me.Datatags.CommandMineRewards;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import me.Datatags.CommandMineRewards.Exceptions.InvalidRegionException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidWorldException;
import me.Datatags.CommandMineRewards.Exceptions.RegionAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.RegionNotInListException;
import me.Datatags.CommandMineRewards.Exceptions.WorldAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.WorldNotInListException;
import me.Datatags.CommandMineRewards.commands.silktouch.SilkTouchRequirement;

public class GlobalConfigManager {
	private static GlobalConfigManager instance = null;
	private CommandMineRewards cmr;
	private GlobalConfigManager(CommandMineRewards cmr) {
		this.cmr = cmr;
	}
	protected static void init(CommandMineRewards cmr) {
		if (instance == null) {
			instance = new GlobalConfigManager(cmr);
		}
	}
	public static GlobalConfigManager getInstance() {
		return instance;
	}
	private ConfigurationSection getConfig() {
		return cmr.getConfig();
	}
	public void load() {
		cmr.saveDefaultConfig();
		cmr.reloadConfig();
		checkOldConfig();
		cmr.saveConfig();
	}
	private void checkOldConfig() {
		// we don't clear the debug log setting because if the plugin is spamming the log
		// the plugin is in verbosity: 2 which no one in their right mind would use except for troubleshooting
		if (getConfig().contains("debug")) {
			getConfig().set("verbosity", getConfig().getBoolean("debug") ? 2 : 1);
			getConfig().set("debug", null);
		}
	}
	public boolean containsIgnoreCase(List<String> list, String search) {
		for (String item : list) {
			if (item.equalsIgnoreCase(search)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsMatch(List<String> list, String match) {
		Pattern pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
		for (String item : list) {
			if (pattern.matcher(item).matches()) {
				return true;
			}
		}
		return false;
	}
	public boolean removeIgnoreCase(List<String> list, String item) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equalsIgnoreCase(item)) {
				list.remove(i);
				return true;
			}
		}
		return false;
	}
	public List<String> getGlobalAllowedWorlds() {
		return getConfig().getStringList("allowedWorlds");
	}
	public void setGlobalAllowedWorlds(List<String> newAllowedWorlds) {
		getConfig().set("allowedWorlds", newAllowedWorlds);
		cmr.saveConfig();
	}
	public void addGlobalAllowedWorld(String world) throws WorldAlreadyInListException, InvalidWorldException {
		if (containsIgnoreCase(getGlobalAllowedWorlds(), world)) {
			throw new WorldAlreadyInListException("The world " + world + " is already globally allowed.");
		}
		if (!isValidWorld(world)) {
			throw new InvalidWorldException("There is no world with the name " + world);
		}
		List<String> worlds = getGlobalAllowedWorlds();
		worlds.add(world);
		setGlobalAllowedWorlds(worlds);
	}
	public void removeGlobalAllowedWorld(String world) throws WorldNotInListException {
		if (!containsIgnoreCase(getGlobalAllowedWorlds(), world)) {
			throw new WorldNotInListException("The world " + world + " is not globally allowed.");
		}
		List<String> worlds = getGlobalAllowedWorlds();
		worlds.remove(world);
		setGlobalAllowedWorlds(worlds);
	}
	public List<String> getGlobalAllowedRegions() {
		return getConfig().getStringList("allowedRegions");
	}
	public void setGlobalAllowedRegions(List<String> newAllowedWorlds) {
		getConfig().set("allowedRegions", newAllowedWorlds);
		cmr.saveConfig();
	}
	public void addGlobalAllowedRegion(String region) throws RegionAlreadyInListException, InvalidRegionException {
		if (containsIgnoreCase(getGlobalAllowedRegions(), region)) {
			throw new RegionAlreadyInListException("The WorldGuard region " + region + " is already globally allowed.");
		}
		if (!cmr.getWGManager().isValidRegion(region)) {
			throw new InvalidRegionException("There is no such WorldGuard region with the name " + region + ".");
		}
		List<String> regions = getGlobalAllowedRegions();
		regions.add(region);
		setGlobalAllowedRegions(regions);
	}
	public void removeGlobalAllowedRegion(String region) throws RegionNotInListException {
		if (!containsIgnoreCase(getGlobalAllowedRegions(), region)) {
			throw new RegionNotInListException("The WorldGuard region " + region + " is not globally allowed!");
		}
		List<String> regions = getGlobalAllowedRegions();
		regions.remove(region);
		setGlobalAllowedRegions(regions);
	}
	public SilkTouchRequirement getGlobalSilkTouchRequirement() {
		return SilkTouchRequirement.getByName(getConfig().getString("silkTouch"));
	}
	public void setGlobalSilkTouchRequirement(SilkTouchRequirement newRequirement) {
		getConfig().set("silkTouch", newRequirement.toString());
		cmr.saveConfig();
	}
	public List<String> getAllowedWorlds(String rewardSection) {
		if (!getConfig().isConfigurationSection(rewardSection)) {
			throw new InvalidRewardSectionException("The reward section " + rewardSection + " does not exist!");
		}
		return getAllowedWorlds(new RewardSection(rewardSection));
	}
	public List<String> getAllowedWorlds(RewardSection rewardSection) {
		if (rewardSection.getAllowedWorlds().size() > 0) {
			return rewardSection.getAllowedWorlds();
		} else if (getGlobalAllowedWorlds().size() > 0) {
			return getGlobalAllowedWorlds();
		} else {
			List<String> rv = new ArrayList<String>();
			rv.add("*");
			return rv;
		}
	}
	public boolean isWorldAllowed(String rewardSection, String world) {
		return isWorldAllowed(new RewardSection(rewardSection), world);
	}
	public boolean isWorldAllowed(RewardSection rewardSection, String world) {
		return getAllowedWorlds(rewardSection).contains("*") || containsIgnoreCase(getAllowedWorlds(rewardSection), world);
		
	}
	public List<String> getAllowedRegions(String rewardSection) {
		return getAllowedRegions(new RewardSection(rewardSection));
	}
	public List<String> getAllowedRegions(RewardSection rewardSection) {
		if (rewardSection.getAllowedRegions() != null) {
			return rewardSection.getAllowedRegions();
		} else if (getGlobalAllowedRegions() != null) {
			return getGlobalAllowedRegions();
		} else {
			List<String> rv = new ArrayList<String>();
			rv.add("*");
			return rv;
		}
	}
	public boolean silkStatusAllowed(String rewardSection, String reward, boolean silkTouch) {
		if (!getConfig().isConfigurationSection(rewardSection)) {
			throw new InvalidRewardSectionException("The reward section " + rewardSection + " does not exist!");
		}
		if (!getConfig().isConfigurationSection(rewardSection + ".rewards." + reward)) {
			throw new InvalidRewardException("The reward " + reward + " does not exist under the ");
		}
		return silkStatusAllowed(new RewardSection(rewardSection), new Reward(rewardSection, reward), silkTouch);
	}
	public SilkTouchRequirement getSilkTouchRequirement(RewardSection rewardSection, Reward reward) {
		if (reward.getSilkTouchRequirement() != null) {
			return reward.getSilkTouchRequirement();
		} else if (rewardSection.getSilkTouchRequirement() != null) {
			return rewardSection.getSilkTouchRequirement();
		} else if (getGlobalSilkTouchRequirement() != null) {
			return getGlobalSilkTouchRequirement();
		} else {
			return SilkTouchRequirement.IGNORED;
		}
	}
	public boolean silkStatusAllowed(RewardSection rewardSection, Reward reward, boolean silkTouch) {
		SilkTouchRequirement current = getSilkTouchRequirement(rewardSection, reward);
		if (current == SilkTouchRequirement.IGNORED) {
			return true;
		} else if (current == SilkTouchRequirement.REQUIRED && silkTouch) {
			return true;
		} else if (current == SilkTouchRequirement.DISALLOWED && !silkTouch) {
			return true;
		} else {
			return false;
		}
	}
	public double getMultiplier() {
		return getConfig().getDouble("multiplier");
	}
	public void setMultiplier(double newMultiplier) {
		getConfig().set("multiplier", newMultiplier);
		cmr.saveConfig();
	}
	public boolean getDebug() {
		return getConfig().getInt("verbosity", 1) > 1;
	}
	public boolean isDebugLog() {
		return getConfig().getBoolean("debugLog");
	}
	public int getVerbosity() {
		return getConfig().getInt("verbosity", 1);
	}
	public boolean getSurvivalOnly() {
		return getConfig().getBoolean("survivalOnly");
	}
	public boolean isValidatingWorldsAndRegions() {
		return getConfig().getBoolean("validateWorldsAndRegions");
	}
	public boolean removeInvalidValues() {
		return getConfig().getBoolean("removeInvalidValues");
	}
	public List<String> getRewardSectionNames() {
		List<String> names = new ArrayList<String>();
		for (String key : cmr.getConfig().getKeys(false)) {
			if (!cmr.getConfig().isConfigurationSection(key)) { 
				continue;
			}
			names.add(key);
		}
		return names;
	}
	public List<RewardSection> getRewardSections() {
		List<RewardSection> rv = new ArrayList<RewardSection>();
		for (String key : cmr.getConfig().getKeys(false)) {
			if (!cmr.getConfig().isConfigurationSection(key)) { 
				continue;
			}
			rv.add(new RewardSection(key));
		}
		return rv;
	}
	public String getPrettyRewardSections() {
		List<String> assembledList = new ArrayList<String>();
		for (String key : cmr.getConfig().getKeys(false)) {
			if (!cmr.getConfig().isConfigurationSection(key)) { 
				continue;
			}
			assembledList.add(key);
		}
		return makePretty(assembledList);
	}
	public boolean isValidWorld(String test) {
		if (!isValidatingWorldsAndRegions()) {
			return true;
		}
		for (World world : Bukkit.getServer().getWorlds()) {
			if (world.getName().equalsIgnoreCase(test)) {
				return true;
			}
		}
		return false;
	}
	public String makePretty(List<String> strings) {
		String assembledList = "";
		for (String item : strings) {
			if (item.equals(strings.get(strings.size() - 1))) { // if item is the last item in the list...
				if (strings.size() > 1) {
					assembledList += "and " + item + ".";
				} else {
					assembledList += item + ".";
				}
				break;
			} else {
				assembledList += item + ", ";
			}
		}
		return assembledList;
	}
	public String searchIgnoreCase(String search, String origin) {
		if (cmr.getConfig().isConfigurationSection(search)) {
			return search;
		}
		if (!cmr.getConfig().isConfigurationSection(origin)) return null;
		for (String key : cmr.getConfig().getConfigurationSection(origin).getKeys(false)) { // search ignoring case
			if (!cmr.getConfig().isConfigurationSection(key)) {
				continue;
			}
			if (key.equalsIgnoreCase(search)) {
				return key;
			}
		}
		return null;
	}
}
