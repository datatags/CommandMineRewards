package me.datatags.commandminerewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.datatags.commandminerewards.Exceptions.CommandNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.Exceptions.RewardAlreadyExistsException;
import me.datatags.commandminerewards.commands.CommandDispatcher;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.commands.special.SpecialCommand;

public class Reward {
	private CommandMineRewards cmr;
	private static final Random RANDOM = new Random();
	private ConfigurationSection group;
	private Permission perm; // the permission required for this reward specifically.  Looks like cmr.use.rewardsection.reward
	private String parentName;
	private GlobalConfigManager gcm;
	private List<RewardCommandEntry> commands;
	private CMRBlockManager cbm;
	public Reward(String parent, String reward, boolean create) throws RewardAlreadyExistsException {
		this(new RewardGroup(parent), reward, create);
	}
	public Reward(RewardGroup parent, String reward, boolean create) throws RewardAlreadyExistsException {
		cmr = CommandMineRewards.getInstance();
		cbm = CMRBlockManager.getInstance();
		if (reward.contains(".")) {
			throw new InvalidRewardGroupException("You cannot use periods in reward names!");
		}
		this.gcm = GlobalConfigManager.getInstance();
		this.parentName = parent.getName();
		boolean rewardExists = gcm.getRewardsConfig().isConfigurationSection(parent.getName() + ".rewards." + reward); 
		if (!rewardExists && !create) {
			if (gcm.searchIgnoreCase(reward, parent.getName() + ".rewards") == null) {
				throw new InvalidRewardException("Reward " + reward + " does not exist under group " + parent.getName() + "!");
			}
			reward = gcm.searchIgnoreCase(reward, parent.getName() + ".rewards");
		} else if (rewardExists && create){
			throw new RewardAlreadyExistsException("Reward " + reward + " already exists under group " + parent.getName() + "!");
		}
		if (!rewardExists && create) {
			this.group = gcm.getRewardsConfig().createSection(parent.getName() + ".rewards." + reward);
			gcm.saveRewardsConfig();
			getParent().loadChild(getName());
		}
		this.group = gcm.getRewardsConfig().getConfigurationSection(parent.getName() + ".rewards." + reward);
		perm = new Permission("cmr.use." + parent.getName() + "." + reward);
		buildCommands();
		if (create) {
			cbm.loadReward(parent, this);
		}
	}
	public Reward(String parent, String reward) throws RewardAlreadyExistsException {
		this(parent, reward, false);
	}
	public Reward(RewardGroup parent, String reward) throws RewardAlreadyExistsException {
		this(parent, reward, false);
	}
	private void buildCommands() {
		commands = new ArrayList<>();
		for (String command : getRawCommands()) {
			if (command.startsWith("!")) {
				String[] split = command.split(" ");
				SpecialCommand scmd = CommandDispatcher.getInstance().getSpecialCommand(split[0].substring(1)); 
				if (scmd == null) {
					CMRLogger.warning("Invalid special command: " + command);
					continue;
				}
				String[] args = Arrays.copyOfRange(split, 1, split.length);
				commands.add(new RewardCommandEntry(scmd, args));
			} else {
				commands.add(new RewardCommandEntry(command));
			}
		}
	}
	public double getRawChance() {
		return this.group.getDouble("chance", 0); // return 0 if no value set
	}
	public double getChance() {
		return Math.min(getRawChance() * gcm.getMultiplier(), 100);
	}
	public void setChance(double newChance) {
		if (newChance > 100) {
			newChance = 100;
		} else if (newChance < 0) {
			newChance = 0;
		}
		set("chance", newChance);
	}
	public List<String> getRawCommands() {
		return this.group.getStringList("commands");
	}
	public List<RewardCommandEntry> getCommands() {
		return commands;
	}
	public void setCommands(List<String> newCommands) {
		set("commands", newCommands);
		buildCommands();
	}
	public void addCommand(String command) {
		List<String> commands = getRawCommands();
		commands.add(command);
		setCommands(commands);
	}
	public void removeCommand(String command) throws CommandNotInListException {
		List<String> commands = getRawCommands();
		if (gcm.removeIgnoreCase(commands, command)) {
			setCommands(commands);
		} else {
			throw new CommandNotInListException("The command " + command + " is not executed by the reward " + this.getName() + "!");
		}
	}
	public void removeCommand(int index) throws ArrayIndexOutOfBoundsException {
		List<String> commands = getRawCommands();
		if (index >= commands.size()) {
			throw new ArrayIndexOutOfBoundsException("The list of commands in " + this.getName() + " does not contain a command with an index of " + index + "!");
		}
		commands.remove(index);
		setCommands(commands);
	}
	public void insertCommand(String command, int index) {
		List<String> commands = getRawCommands();
		commands.add(index, command);
		setCommands(commands);
	}
	public SilkTouchPolicy getSilkTouchPolicy() {
		SilkTouchPolicy stp = SilkTouchPolicy.getByName(this.group.getString("silkTouch"));
		return stp == null ? SilkTouchPolicy.INHERIT : stp;
	}
	public void setSilkTouchPolicy(SilkTouchPolicy silkTouch) {
		set("silkTouch", silkTouch.toString());
	}
	public void delete() {
		gcm.getRewardsConfig().set(this.getPath(), null);
		gcm.saveRewardsConfig();
		getParent().unloadChild(getName());
		cbm.unloadReward(getParent(), getName());
	}
	public String getName() {
		return this.group.getName();
	}
	public String getPath() {
		return this.group.getCurrentPath();
	}
	private void set(String path, Object newValue) {
		this.group.set(path, newValue);
		gcm.saveRewardsConfig();
		getParent().reloadChild(getName());
		cbm.reloadReward(getParent(), this);
	}
	public boolean hasPermission(Player player) {
		return player.hasPermission(perm);
	}
	public Permission getPerm() {
		return this.perm;
	}
	public RewardGroup getParent() {
		for (RewardGroup group : cbm.getGroupCache()) {
			if (group.getName().equals(parentName)) {
				return group;
			}
		}
		CMRLogger.warning("Couldn't find parent for reward " + this.getName());
		return null;
	}
	private void debug(String msg) {
		CMRLogger.debug(msg);
	}
	public boolean isApplicable(Player player) {
		// does not check things that should be checked by RewardGroup
		if (this.getChance() == 0) {
			debug("Warning! Chance property was 0, invalid, or non-existant in reward " + this.getName() + " in group " + getParent().getName());
			return false;
		}
		if (!hasPermission(player)) {
			debug("Player " + player.getName() + " did not receive reward " + this.getName() + " for lack of permission!");
			return false;
		}
		// not if (SO and gamemode are correct) or we don't care
		if (gcm.getSurvivalOnly() && player.getGameMode() != GameMode.SURVIVAL) {
			debug("Player " + player.getName() + " did not receive reward " + this.getName() + " because they were in the wrong game mode!");
			return false;
		}
		if (cmr.getItemInHand(player) == null) {
			if (!gcm.silkStatusAllowed(getParent(), this, false)) {
				debug("Player was denied access to reward because of the presence or absence of silk touch");
				return false;
			}
		} else {
			if (!gcm.silkStatusAllowed(getParent(), this, cmr.getItemInHand(player).getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0)) {
				debug("Player was denied access to reward because of the presence or absence of silk touch");
				return false;
			}
		}
		return true;
	}
	public boolean execute(Player target) {
		return execute(target, false);
	}
	public boolean execute(Player target, boolean skipChecks) {
		debug("Processing reward " + this.getName());
		double randomNumber = RANDOM.nextDouble() * 100; 
		debug("Random: " + randomNumber);
		if (skipChecks || randomNumber < getChance()) {
			if (skipChecks) {
				debug("Skipping dice roll and executing");
			} else {
				debug(randomNumber + " < " + getChance() + ", executing reward");
			}
			for (RewardCommandEntry command : getCommands()) {
				command.execute(target);
			}
			return true;
		} else {
			debug(randomNumber + " !< " + getChance() + ", doing nothing");
			return false;
		}
	}
}
