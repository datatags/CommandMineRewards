package me.AlanZ.CommandMineRewards;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.AlanZ.CommandMineRewards.Exceptions.CommandAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.CommandNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.RewardAlreadyExistsException;

public class Reward {
	public static CommandMineRewards cmr = null;
	private ConfigurationSection section;
	private Permission perm; // the permission required for this reward specifically.  Looks like cmr.use.rewardsection.reward
	private Permission parentPerm; // the permission required for the parent reward section of this reward.  Looks like cmr.use.rewardsection.*
	private static final Permission allPerm = new Permission("cmr.use.*"); // the permission required to use any reward under any reward section.  Always the same.
	public Reward(String parent, String reward, boolean createIfNotFound) throws RewardAlreadyExistsException {
		if (cmr == null) {
			throw new IllegalStateException("CMR instance has not been set!");
		}
		if (parent.contains(".") || reward.contains(".")) {
			throw new InvalidRewardSectionException("You cannot use periods in reward section names!");
		}
		if (!cmr.getConfig().isConfigurationSection(parent) && !createIfNotFound) { // if we couldn't find it easily and we're not supposed to create it,
			if (GlobalConfigManager.searchIgnoreCase(parent, "") == null) { // search for it
				throw new InvalidRewardSectionException("Reward section " + parent + " does not exist!"); // if we still couldn't find it, throw an exception
			} else {
				parent = GlobalConfigManager.searchIgnoreCase(parent, "");
			}
		}
		if (!cmr.getConfig().isConfigurationSection(parent + ".rewards." + reward) && !createIfNotFound) {
			if (GlobalConfigManager.searchIgnoreCase(reward, parent + ".rewards") == null) {
				throw new InvalidRewardException("Reward " + reward + " does not exist under section " + parent + "!");
			} else {
				reward = GlobalConfigManager.searchIgnoreCase(reward, parent + ".rewards");
			}
		} else if (cmr.getConfig().isConfigurationSection(parent + ".rewards." + reward) && createIfNotFound){
			throw new RewardAlreadyExistsException("Reward " + reward + " already exists under section " + parent + "!");
		}
		if (!cmr.getConfig().isConfigurationSection(parent + ".rewards." + reward) && createIfNotFound) {
			cmr.getConfig().createSection(parent + ".rewards." + reward);
			cmr.saveConfig();
		}
		this.section = cmr.getConfig().getConfigurationSection(parent + ".rewards." + reward);
		parentPerm = new Permission("cmr.use." + parent + ".*");
		perm = new Permission("cmr.use." + parent + "." + reward);
	}
	public Reward(RewardSection parent, String reward, boolean createIfNotFound) throws RewardAlreadyExistsException {
		this(parent.getName(), reward, createIfNotFound);
	}
	public Reward(String parent, String reward) throws RewardAlreadyExistsException {
		this(parent, reward, false);
	}
	public Reward(RewardSection parent, String reward) throws RewardAlreadyExistsException {
		this(parent, reward, false);
	}
	public Reward(ConfigurationSection reward) {
		this.section = reward;
	}
	public double getChance() {
		return this.section.getDouble("chance", 0); // return 0 if no value set
	}
	public void setChance(double newChance) {
		if (newChance > 100) {
			newChance = 100;
		} else if (newChance < 0) {
			newChance = 0;
		}
		setValue("chance", newChance);
	}
	public List<String> getCommands() {
		return this.section.getStringList("commands");
	}
	public void setCommands(List<String> newCommands) {
		setValue("commands", newCommands);
	}
	public void addCommand(String command) throws CommandAlreadyInListException {
		if (GlobalConfigManager.containsIgnoreCase(this.getCommands(), command)) {
			throw new CommandAlreadyInListException("The command " + command + " is already being executed by the reward " + this.getName() + "!");
		}
		List<String> commands = getCommands();
		commands.add(command);
		setCommands(commands);
	}
	public void removeCommand(String command) throws CommandNotInListException {
		List<String> commands = getCommands();
		if (GlobalConfigManager.removeIgnoreCase(commands, command)) {
			setCommands(commands);
		} else {
			throw new CommandNotInListException("The command " + command + " is not executed by the reward " + this.getName() + "!");
		}
	}
	public void removeCommand(int index) throws ArrayIndexOutOfBoundsException {
		List<String> commands = getCommands();
		if (index >= commands.size()) {
			throw new ArrayIndexOutOfBoundsException("The list of commands in " + this.getName() + " does not contain a command with an index of " + index + "!");
		}
		commands.remove(index);
		setCommands(commands);
	}
	public void insertCommand(String command, int index) throws CommandAlreadyInListException {
		List<String> commands = getCommands();
		if (GlobalConfigManager.containsIgnoreCase(commands, command)) {
			throw new CommandAlreadyInListException("The command " + command + " is already being executed by the reward " + this.getName() + "!");
		}
		commands.add(index, command);
		setCommands(commands);
	}
	public SilkTouchRequirement getSilkTouchRequirement() {
		return SilkTouchRequirement.getByName(this.section.getString("silkTouch"));
	}
	public void setSilkTouchRequirement(SilkTouchRequirement silkTouch) {
		setValue("silkTouch", silkTouch.toString());
	}
	public void delete() {
		cmr.getConfig().set(this.getPath(), null);
	}
	public String getName() {
		return this.section.getName();
	}
	public String getPath() {
		return this.section.getCurrentPath();
	}
	private void setValue(String path, Object newValue) {
		this.section.set(path, newValue);
		cmr.saveConfig();
	}
	public boolean hasPermission(Player player) {
		if (player.hasPermission(perm) || player.hasPermission(parentPerm) || player.hasPermission(allPerm)) {
			return true;
		} else {
			return false;
		}
	}
	public Permission getPerm() {
		return this.perm;
	}
}
