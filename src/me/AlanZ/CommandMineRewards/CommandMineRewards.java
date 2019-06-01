package me.AlanZ.CommandMineRewards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.AlanZ.CommandMineRewards.Exceptions.BlockAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.BlockNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.CommandAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.CommandNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidMaterialException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRegionException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.RewardAlreadyExistsException;
import me.AlanZ.CommandMineRewards.Exceptions.RewardSectionAlreadyExistsException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldNotInListException;
import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand;
import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand_1_8;
import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand_1_9;

public class CommandMineRewards extends JavaPlugin {
	
	public Permission allRewardsPermission = new Permission("cmr.use.*");
	private Permission helpPermission = new Permission("cmr.help");
	private Permission reloadPermission = new Permission("cmr.reload");
	private Permission viewMultiplierPermission = new Permission("cmr.multplier.view");
	private Permission modifyMultiplierPermission = new Permission("cmr.multiplier.modify");
	private Permission viewBlocksPermission = new Permission("cmr.blocks.view");
	private Permission modifyBlocksPermission = new Permission("cmr.blocks.modify");
	private Permission viewRewardsPermission = new Permission("cmr.rewards.view");
	private Permission modifyRewardsPermission = new Permission("cmr.rewards.modify");
	private Permission viewRewardCommands = new Permission("cmr.commands.view");
	private Permission modifyRewardCommands = new Permission("cmr.commands.modify");
	private Permission viewChancePermission = new Permission("cmr.chance.view");
	private Permission modifyChancePermission = new Permission("cmr.chance.modify");
	private Permission viewAllowedWorldsPermission = new Permission("cmr.worlds.view");
	private Permission modifyAllowedWorldsPermission = new Permission("cmr.worlds.modify");
	private Permission viewAllowedRegionsPermission = new Permission("cmr.regions.view");
	private Permission modifyAllowedRegionsPermission = new Permission("cmr.regions.modify");
	private Permission viewSilkTouchPolicyPermission = new Permission("cmr.silktouchpolicy.view");
	private Permission modifySilkTouchPolicyPermission = new Permission("cmr.silktouchpolicy.modify");
	private String noPermissionMessage = ChatColor.RED + "You do not have permission to use this command!";
	private String internalErrorMessage = ChatColor.RED + "An internal error has occurred.  Please ask an admin to check the log.";
	
	PluginManager pm = getServer().getPluginManager();
	
	List<String> defBlocks = new ArrayList<String>();
	List<String> rewardsWithPermissions = new ArrayList<String>();
	boolean removeInvalidValues = false;
	private ItemInHand iih = null;
	private boolean worldGuardLoaded = false;
	private Map<String,Permission> commandPermissions = new HashMap<String,Permission>();
	private Map<String,String> commandBasicDescription = new LinkedHashMap<String,String>();
	private Map<String,String> commandExtensiveDescription = new LinkedHashMap<String,String>();
	private Map<String,String> commandUsage = new LinkedHashMap<String,String>();
	private Map<String,Integer> commandMinArgs = new HashMap<String,Integer>();
	private Map<String,Integer> commandMaxArgs = new HashMap<String,Integer>();
	private File debugLog = null;
	private final int helpPages = 7; // number of pages in help command
	
	private void initCommands() {
		commandBasicDescription.put("reload", "Reload the config");
		commandBasicDescription.put("multiplier", "Change the reward chance multiplier");
		commandBasicDescription.put("help", "See command list and descriptions");
		commandBasicDescription.put("addblock", "Add a reward-triggering block");
		commandBasicDescription.put("removeblock", "Remove a reward-triggering block");
		commandBasicDescription.put("listblocks", "List the blocks that trigger rewards");
		commandBasicDescription.put("addreward", "Add a reward");
		commandBasicDescription.put("removereward", "Delete a reward");
		commandBasicDescription.put("listrewards", "List all defined rewards");
		commandBasicDescription.put("addcommand", "Add a command to the specified reward");
		commandBasicDescription.put("insertcommand", "Add a command to the middle of a list");
		commandBasicDescription.put("removecommand", "Remove a command by index");
		commandBasicDescription.put("listcommands", "Show commands that a reward runs");
		commandBasicDescription.put("addworld", "Add an allowed world");
		commandBasicDescription.put("addcurrentworld", "Make the world you're in allowed for rewards");
		commandBasicDescription.put("removeworld", "Remove an allowed world");
		commandBasicDescription.put("removecurrentworld", "Remove the world you're in from triggering rewards.");
		commandBasicDescription.put("listworlds", "Show allowed worlds");
		commandBasicDescription.put("addregion", "Add an allowed region");
		commandBasicDescription.put("removeregion", "Remove an allowed region");
		commandBasicDescription.put("listregions", "Show allowed regions");
		commandBasicDescription.put("setsilktouchpolicy", "Changes the policy on silk touch");
		commandBasicDescription.put("viewsilktouchpolicy", "Views the policy on silk touch");
		commandBasicDescription.put("chance", "See or change the chance of being rewarded.");
		
		commandExtensiveDescription.put("reload", "Simply reloads the CMR config.  All options, rewards, and sections will be reloaded.  All block lists will be re-checked for invalid blocks.");
		commandExtensiveDescription.put("multiplier", "If a chance argument is specified, update the chance of the specified reward. Otherwise, read the chance of the specified reward.");
		commandExtensiveDescription.put("help", "Get help with commands. If no argument is specified, it will read page 1 of help. If a number argument is specified, it will read that page of the help. If a CMR command is specified, it will read the detailed help for that command (as you see now.)");
		commandExtensiveDescription.put("addblock", "Add a block as reward-triggering to the specified reward section.");
		commandExtensiveDescription.put("removeblock", "Remove a reward-triggering block from the block list of the specified section.");
		commandExtensiveDescription.put("listblocks", "Lists all blocks that trigger rewards in the specified section.");
		commandExtensiveDescription.put("addreward", "If two arguments are specified, add a new reward under the (existing) specified reward section.  If one argument is specified, it creates a reward section with the specified name.");
		commandExtensiveDescription.put("removereward", "If two arguments are specified, delete a reward under the specified reward section.  If one argument is specified, deletes the ENTIRE reward section, even if the reward section has rewards in it.  Be careful!");
		commandExtensiveDescription.put("listrewards", "If no arguments are specified, lists all reward sections.  If a reward section is specified, lists all rewards under that section.");
		commandExtensiveDescription.put("addcommand", "Add a command to the specified reward. It will go onto the end of the list. If you want it to go in a different position in the list, see insertcommand.");
		commandExtensiveDescription.put("insertcommand", "Inserts a command into a reward's command list at the specified ID, meaning the command will now hold the specified ID, shifting others down.  You can insert a command at the beginning of the list by specifying an ID of 0. You can see command IDs, or indexes, with listcommands");
		commandExtensiveDescription.put("removecommand", "Removes the command that holds the ID or index specified in the specified reward.  You can get this ID from listcommands");
		commandExtensiveDescription.put("listcommands", "Shows all commands that the specified reward runs when it is triggered, and their respective ID or index numbers that can be used for inserting or deleting commands.  Note that the first index is 0.");
		commandExtensiveDescription.put("addworld", "If a reward section is specified, adds the specified world to the list of allowed worlds.  If a reward section is not specified, adds the specified world to the globally allowed worlds list.");
		commandExtensiveDescription.put("addcurrentworld", "Adds the world you are currently in to the list of allowed worlds of a reward section, or globally if a reward section is not specified..");
		commandExtensiveDescription.put("removeworld", "Removes a world from the list of allowed worlds of a reward section, or from the global list globally.");
		commandExtensiveDescription.put("removecurrentworld", "Removes the world you are currently in from the list of allowed worlds of a reward section, or globally.");
		commandExtensiveDescription.put("listworlds", "Shows the list of allowed worlds of a reward section, or globally.");
		commandExtensiveDescription.put("addregion", "Adds a region to the list of allowed regions of a reward section, or the global list if no reward section is specified.");
		commandExtensiveDescription.put("removeregion", "Removes a region from the list of allowed regions of a reward section, or the global list if no reward section is specified.");
		commandExtensiveDescription.put("listregions", "Shows the list of allowed regions of a reward section, or globally if no reward section specified.");
		commandExtensiveDescription.put("setsilktouchpolicy", "Changes the silk touch policy on a reward, or reward section if no reward specified, or globally if no reward or reward section specified.");
		commandExtensiveDescription.put("viewsilktouchpolicy", "Observes the silk touch policy of a reward, or reward section if no reward specified, or globally if no reward or reward section specified.");
		commandExtensiveDescription.put("chance", "If chance argument is given, change the chance of a reward triggering. Otherwise, read the chance of it triggering.  This number should be a percentage chance between 0 and 100, decimals allowed.");
		
		commandUsage.put("reload", "");
		commandUsage.put("multiplier", "[multiplier]");
		commandUsage.put("help", "[page]");
		commandUsage.put("addblock", "<rewardSection> [block]");
		commandUsage.put("removeblock", "<rewardSection> [block]");
		commandUsage.put("listblocks", "<rewardSection>");
		commandUsage.put("addreward", "<rewardSection> [reward]");
		commandUsage.put("removereward", "<rewardSection> [reward]");
		commandUsage.put("listrewards", "[rewardSection]");
		commandUsage.put("addcommand", "<rewardSection> <reward> <command>");
		commandUsage.put("insertcommand", "<rewardSection> <reward> <insert index> <command>");
		commandUsage.put("removecommand", "<rewardSection> <reward> <id>");
		commandUsage.put("listcommands", "<rewardSection> <reward>");
		commandUsage.put("addworld", "[rewardSection] <world>");
		commandUsage.put("addcurrentworld", "[rewardSection]");
		commandUsage.put("removeworld", "[rewardSection] <world>");
		commandUsage.put("removecurrentworld", "[rewardSection]");
		commandUsage.put("listworlds", "[rewardSection]");
		commandUsage.put("addregion", "[rewardSection] <region>");
		commandUsage.put("removeregion", "[rewardSection] <region>");
		commandUsage.put("listregions", "[rewardSection]");
		commandUsage.put("setsilktouchpolicy", "[rewardSection] [reward] <ALLOWED|IGNORED|DISALLOWED>");
		commandUsage.put("viewsilktouchpolicy", "[rewardSection] [reward]");
		commandUsage.put("chance", "<rewardSection> <reward> [chance]");
		
		cmdMinMax("reload", 1, 1);
		cmdMinMax("help", 1, 2);
		cmdMinMax("multiplier", 1, 2);
		cmdMinMax("addblock", 2, 4);
		cmdMinMax("removeblock", 2, 4);
		cmdMinMax("listblocks", 2, 2);
		cmdMinMax("addreward", 2, 3);
		cmdMinMax("removereward", 2, 3);
		cmdMinMax("listrewards", 1, 2);
		cmdMinMax("addcommand", 4, -1);
		cmdMinMax("insertcommand", 5, -1);
		cmdMinMax("removecommand", 4, -1);
		cmdMinMax("listcommands", 3, 3);
		cmdMinMax("addworld", 2, 3);
		cmdMinMax("addcurrentworld", 1, 2);
		cmdMinMax("removeworld", 2, 3);
		cmdMinMax("removecurrentworld", 1, 2);
		cmdMinMax("listworlds", 1, 2);
		cmdMinMax("addregion", 2, 3);
		cmdMinMax("removeregion", 2, 3);
		cmdMinMax("listregions", 1, 2);
		cmdMinMax("setsilktouchpolicy", 2, 4);
		cmdMinMax("viewsilktouchpolicy", 1, 3);
		cmdMinMax("chance", 3, 4);
		
		commandPermissions.put("reload", reloadPermission);
		commandPermissions.put("help", helpPermission);
		commandPermissions.put("multiplier", viewMultiplierPermission);
		commandPermissions.put("listblocks", viewBlocksPermission);
		commandPermissions.put("listrewards", viewRewardsPermission);
		commandPermissions.put("listcommands", viewRewardCommands);
		commandPermissions.put("chance", viewChancePermission);
		commandPermissions.put("addblock", modifyBlocksPermission);
		commandPermissions.put("removeblock", modifyBlocksPermission);
		commandPermissions.put("addreward", modifyRewardsPermission);
		commandPermissions.put("removereward", modifyRewardsPermission);
		commandPermissions.put("addcommand", modifyRewardCommands);
		commandPermissions.put("insertcommand", modifyRewardCommands);
		commandPermissions.put("removecommand", modifyRewardCommands);
		commandPermissions.put("addworld", modifyAllowedWorldsPermission);
		commandPermissions.put("addcurrentworld", modifyAllowedWorldsPermission);
		commandPermissions.put("removeworld", modifyAllowedWorldsPermission);
		commandPermissions.put("removecurrentworld", modifyAllowedWorldsPermission);
		commandPermissions.put("listworlds", viewAllowedWorldsPermission);
		commandPermissions.put("addregion", modifyAllowedRegionsPermission);
		commandPermissions.put("removeregion", modifyAllowedRegionsPermission);
		commandPermissions.put("listregions", viewAllowedRegionsPermission);
		commandPermissions.put("setsilktouchpolicy", modifySilkTouchPolicyPermission);
		commandPermissions.put("viewsilktouchpolicy", viewSilkTouchPolicyPermission);
	}
	private void cmdMinMax(String command, int min, int max) {
		commandMinArgs.put(command, min);
		commandMaxArgs.put(command, max);
	}
	private void initDebugLog() {
		if (GlobalConfigManager.isDebugLog() && debugLog == null) {
			try {
				File log = new File(this.getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					getLogger().info("CMR debug log was successfully created!");
				}
				debugLog = log;
				SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
				debug("CMR has started up at " + asdf.format(new Date()));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				getLogger().warning("Failed to create CMR debug log. Do we have write permissions?");
				//e.printStackTrace();
			}
		}
	}
	@Override
	public void onEnable() {
		/*defBlocks.add("stone");
		defBlocks.add("cobblestone");
		defBlocks.add("mossy_cobblestone");
		this.getConfig().addDefault("Blocks", defBlocks);
		this.getConfig().addDefault("multiplier", 1);
		this.getConfig().addDefault("survivalOnly", true);
		this.getConfig().addDefault("debug", false);
		this.getConfig().options().copyDefaults(true);*/
		this.getCommand("cmr").setTabCompleter(new CMRTabComplete(this, commandPermissions));
		GlobalConfigManager.cmr = this;
		initDebugLog();
		RewardSection.cmr = this;
		Reward.cmr = this;
		initCommands();
		saveDefaultConfig();
		RewardSection.fillCache();
		checkOldConfig();
		reload();
		if (isWorldGuardLoaded()) {
			getLogger().info("Found WorldGuard.  Using to check regions.");
			worldGuardLoaded = true;
		} else {
			getLogger().info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
			worldGuardLoaded = false;
		}
		new EventListener(this);
		if (!initItemInHand()) {
			getLogger().severe("Could not determine server version, plugin will not work properly!");
		}
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
	}
	private boolean isWorldGuardLoaded() { // internal
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return false;
	    }
	    return true;
	}
	public boolean usingWorldGuard() { // public
		return worldGuardLoaded;
	}
	private boolean initItemInHand() {
		String version;
		try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
		debug(version);
		if (version.matches("v1_[78]_R.")) {
			getLogger().info("You seem to be running a pre-1.9 version.");
			iih = new ItemInHand_1_8();
		} else {
			getLogger().info("You seem to be running 1.9 or later.");
			iih = new ItemInHand_1_9();
		}
		return true;
	}
	@Override
	public void onDisable() {
		SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
		debug("CMR has been shut down at " + asdf.format(new Date()) + "\n\n\n\n", false);
		getLogger().info("CommandMineRewards (by AlanZ) has been disabled!");
	}
	private void checkOldConfig() {
		if (this.getConfig().isConfigurationSection("Rewards") && this.getConfig().isList("Blocks")) {
			getLogger().info("Found old config, attempting to convert...");
			this.getConfig().set("removeInvalidValues", false);
			if (!this.getConfig().isConfigurationSection("Rewards.rewards")) {
				this.getConfig().createSection("Rewards.rewards");
			}
			for (ConfigurationSection section : getConfigSections("Rewards")) {
				if (section.getName().equals("rewards")) {
					continue;
				}
				this.getConfig().createSection("Rewards.rewards." + section.getName(), section.getValues(false)); // move it. shouldn't need to go deep
				this.getConfig().set(section.getCurrentPath(), null); // delete the old one
			}
			List<String> blocks = this.getConfig().getStringList("Blocks");
			this.getConfig().set("Rewards.blocks", blocks);
			this.getConfig().set("Blocks", null);
			saveConfig();
			getLogger().info("Successfully converted!");
		}
	}
	public void reload() {
		reloadConfig();
		initDebugLog();
		removeInvalidValues = this.getConfig().getBoolean("removeInvalidValues");
		getLogger().info("Checking for invalid items in blocks lists...");
		for (ConfigurationSection work : getConfigSections("")) {
			debug("work = " + work.getName());
			RewardSection section = new RewardSection(work.getName());
			List<String> newBlocks = new ArrayList<String>();
			for (String block : section.getBlocks()) {
				if (isMaterial(block)) {
					if (removeInvalidValues) {
						newBlocks.add(block);
					}
				}
			}
			for (Reward reward : section.getChildren()) {
				if (!rewardsWithPermissions.contains(section.getName() + "." + reward.getName())) {
					debug("Adding permission cmr.use." + section.getName() + "." + reward.getName());
					pm.addPermission(new Permission("cmr.use." + section.getName() + "." + reward.getName()));
					rewardsWithPermissions.add(section.getName() + "." + reward.getName());
				}
			}
			if (removeInvalidValues && newBlocks.size() != section.getBlocks().size()) {
				section.setBlocks(newBlocks);
			}
		}
		if (this.getConfig().contains("debug")) {
			this.getConfig().set("verbosity", this.getConfig().getBoolean("debug") ? 2 : 1);
			this.getConfig().set("debug", null);
		}
		if (this.getConfig().contains("debuglog")) {
			this.getConfig().set("debuglog", false);
		}
		saveConfig();
	}
	public void debug(String msg) {
		debug(msg, true);
	}
	public void debug(String msg, boolean logToConsole) {
		if (GlobalConfigManager.getDebug()) {
			if (logToConsole) getLogger().info(msg);
			if (GlobalConfigManager.isDebugLog()) {
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter(debugLog, true));
					bf.append(msg + "\n");
					bf.close();
				} catch (IOException e) {
					getLogger().severe("Failed to write to CMR debug log! Do we have write permission?");
					e.printStackTrace();
				}
			}
		}
	}
	private boolean validateCommand(String[] args, CommandSender sender) {
		aliasHelper(args);
		String command = args[0];
		if (!commandBasicDescription.containsKey(command.toLowerCase())) {
			debug("Command " + command + " was passed to plugin but has no handler!");
			sender.sendMessage(ChatColor.RED + "Unknown CMR command.  Type /cmr help for help.");
			return false;
		}
		if (!sender.hasPermission(commandPermissions.get(command.toLowerCase()))) {
			sender.sendMessage(noPermissionMessage);
			return false;
		}
		if (args.length < commandMinArgs.get(command.toLowerCase()) || (args.length > commandMaxArgs.get(command.toLowerCase()) && commandMaxArgs.get(command.toLowerCase()) > -1)) {
			sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr " + command.toLowerCase() + " " + commandUsage.get(command.toLowerCase()));
			return false;
		}
		return true;
	}
	private boolean isMaterial(String mat) {
		//if (Material.valueOf(mat.toUpperCase()).isBlock()) {
		if (Material.matchMaterial(mat.split(":", 2)[0]) == null) {
			debug("Item " + mat + " of Blocks is not a valid material!  " + (removeInvalidValues ? "Removing." : "Ignoring."));
			return false;
		}
		if (Material.matchMaterial(mat.split(":", 2)[0]).isBlock()) {
			debug(mat.split(":", 2)[0] + ".isBlock() = true");
			return true;
		} else {
			getLogger().info("Item " + mat + " of Blocks is a material, but not a block!  " + (removeInvalidValues ? "Removing." : "Ignoring."));
			return false;
		}
	}
	public List<ConfigurationSection> getConfigSections(String origin) {
		return getConfigSections(this.getConfig().getConfigurationSection(origin));
	}
	public List<ConfigurationSection> getConfigSections(ConfigurationSection origin) {
		List<ConfigurationSection> sections = new ArrayList<ConfigurationSection>();
		for (String key : origin.getKeys(false)) {
			if (origin.isConfigurationSection(key)) {
				sections.add(origin.getConfigurationSection(key));
			}
		}
		return sections;
	}
	private String parseCommand(int startIndex, String[] args) {
		String command = "";
		for (int i = startIndex; i < args.length; i++) {
			command += args[i] + " ";
		}
		if (command.startsWith("/")) {
			command = command.substring(1, command.length() - 1); // remove slash and trailing space
		} else {
			command = command.substring(0, command.length() - 1); // remove trailing space
		}
		return command;
	}
	public ItemStack getItemInHand(Player player) {
		return iih.getItemInHand(player);
	}
	private void aliasHelper(String[] args) { // set any aliases / shorthands to the full command name to make stuff easier.
		if (args[0].equalsIgnoreCase("sstp")) {
			args[0] = "setsilktouchpolicy";
		} else if (args[0].equalsIgnoreCase("vstp")) {
			args[0] = "viewsilktouchpolicy";
		}
	}
	private void printCommand(String command, CommandSender sender) {
		if (!commandUsage.containsKey(command)) {
			getLogger().warning("Couldn't find usage information for command /cmr " + command);
			sender.sendMessage(internalErrorMessage);
			return;
		}
		sender.sendMessage(ChatColor.GOLD + "/cmr " + command + ": " + ChatColor.GREEN + commandBasicDescription.get(command));
	}
	private void printHelpHeader(int page, CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards" + (page == 1 ? "" : " - Page " + page) + ChatColor.GREEN + "----------");
	}
	private void printHelpFooter(int page, CommandSender sender) {
		if (helpPages <= page) { // if last page, or a page past what was expected
			sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page " + page + ChatColor.GREEN + "----------");
		} else {
			sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page " + page + " - /cmr help " + (page + 1) + " for next page" + ChatColor.GREEN + "----------");
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("cmr")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "This server is running " + ChatColor.GOLD + "CommandMineRewards v" + this.getDescription().getVersion() + ChatColor.GREEN + " by AlanZ");
				sender.sendMessage(ChatColor.GREEN + "Do " + ChatColor.GOLD + "/cmr help" + ChatColor.GREEN + " for commands.");
				return true;
			} else if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1 || args[1].equals("1")) {
					if (sender instanceof ConsoleCommandSender) { // dump all commands on console since they have better scrolling and/or larger window size
						
						return true;
					}
					printHelpHeader(1, sender);
					sender.sendMessage(ChatColor.GOLD + "/cmr: " + ChatColor.GREEN + "Displays information about the plugin.");
					printCommand("help", sender);
					printCommand("reload", sender);
					printCommand("multiplier", sender);
					printCommand("addblock", sender);
					printCommand("removeblock", sender);
					printHelpFooter(1, sender);
				} else if (args[1].equals("2")) {
					printHelpHeader(2, sender);
					printCommand("listblocks", sender);
					printCommand("addreward", sender);
					printCommand("removereward", sender);
					printCommand("listrewards", sender);
					printCommand("addcommand", sender);
					printHelpFooter(2, sender);
				} else if (args[1].equals("3")) {
					printHelpHeader(3, sender);
					printCommand("insertcommand", sender);
					printCommand("removecommand", sender);
					printHelpFooter(3, sender);
				} else if (args[1].equals("4")) {
					printHelpHeader(4, sender);
					printCommand("listcommands", sender);
					printCommand("addworld", sender);
					printCommand("removeworld", sender);
					printHelpFooter(4, sender);
				} else if (args[1].equals("5")) {
					printHelpHeader(5, sender);
					printCommand("listworlds", sender);
					printCommand("addregion", sender);
					printCommand("removeregion", sender);
					printHelpFooter(5, sender);
				} else if (args[1].equals("6")) {
					printHelpHeader(6, sender);
					printCommand("listregions", sender);
					printCommand("setsilktouchpolicy", sender);
					printCommand("viewsilktouchpolicy", sender);
					printHelpFooter(6, sender);
				} else if (args[1].equals("7")) {
					printHelpHeader(7, sender);
					printCommand("chance", sender);
					printHelpFooter(7, sender);
				} else if (commandExtensiveDescription.containsKey(args[1].toLowerCase())) {
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards Help" + ChatColor.GREEN + "----------");
					sender.sendMessage(ChatColor.GOLD + "Usage:  " + ChatColor.GREEN + "/cmr " + args[1].toLowerCase() + " " + commandUsage.get(args[1].toLowerCase()));
					sender.sendMessage(ChatColor.GOLD + "Description:  " + ChatColor.GREEN + commandExtensiveDescription.get(args[1]));
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards Help" + ChatColor.GREEN + "----------");
				} else {
					sender.sendMessage(ChatColor.RED + "Please enter a valid page number from 1 to " + helpPages + ", or a CMR command.");
				}
				return true;
			}
			if (!validateCommand(args, sender)) {
				return true;
			}
			if (args[0].equalsIgnoreCase("multiplier")) {
				if (args.length == 2) {
					if (sender.hasPermission(modifyMultiplierPermission)) {
						double multiplier;
						try {
							multiplier = Double.parseDouble(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
							return true;
						}
						GlobalConfigManager.setMultiplier(multiplier);
						sender.sendMessage(ChatColor.GREEN + "Multiplier successfully updated!  New multiplier:  " + multiplier);
					} else {
						sender.sendMessage(noPermissionMessage);
					}
				} else if (args.length == 1) {
					sender.sendMessage(ChatColor.YELLOW + "Current multiplier:  " + GlobalConfigManager.getMultiplier());
				}
			} else if (args[0].equalsIgnoreCase("reload")) {
				reload();
				sender.sendMessage(ChatColor.GREEN + "Config successfully reloaded.");
			}
			/* else if (args[0].equalsIgnoreCase("debug")) {
				if (sender.hasPermission(debugPermission)) {
					sender.sendMessage(ChatColor.YELLOW + "debug:  " + debug + "  multiplier:  " + multiplier);
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			}*/ else if (args[0].equalsIgnoreCase("addblock")) {
				if (args.length == 2) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						Material item = player.getInventory().getItemInMainHand().getType();
						if (item.isBlock() && item != Material.AIR) {
							try {
								new RewardSection(args[1]).addBlock(item);
							} catch (InvalidRewardSectionException | BlockAlreadyInListException | InvalidMaterialException e) {
								sender.sendMessage(ChatColor.RED + e.getMessage());
								return true;
							}
							player.sendMessage(ChatColor.GREEN + "Item " + item.toString().toLowerCase() + " added to blocks list.");
						} else {
							player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block to add or manually specify one.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Please specify a block to add to the list.");
					}
				} else if (args.length == 3 || args.length == 4) {
					try {
						if (args.length == 3) {
							new RewardSection(args[1]).addBlock(args[2].toLowerCase());
						} else if (args.length == 4) {
							new RewardSection(args[1]).addBlock(args[2].toLowerCase(), args[3].toLowerCase());
						}
					} catch (InvalidRewardSectionException | BlockAlreadyInListException | InvalidMaterialException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + args[2].toLowerCase() + " successfully added to blocks list!");
				}
			} else if (args[0].equalsIgnoreCase("removeblock")) {
				if (args.length == 2) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						Material item = player.getInventory().getItemInMainHand().getType();
						if (item.isBlock() && item != Material.AIR) {
							try {
								new RewardSection(args[1]).removeBlock(item);
							} catch (InvalidRewardSectionException | BlockNotInListException e) {
								sender.sendMessage(ChatColor.RED + e.getMessage());
								return true;
							}
							player.sendMessage(ChatColor.GREEN + "The item " + item.toString().toLowerCase() + " was successfully removed.");
						} else {
							player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block or manually specify a block to remove.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Please specify a block to remove from the list.");
					}
					return true;
				} else if (args.length == 3) {
					try {
						new RewardSection(args[1]).removeBlock(args[2]);
					} catch (InvalidRewardSectionException | BlockNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "The item " + args[2].toLowerCase() + " was successfully removed.");
				} else if (args.length == 4) {
					byte data;
					if (args[3].equals("*")) {
						data = Byte.MAX_VALUE;
					} else {
						try {
							data = Byte.parseByte(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Invalid data value!");
							return true;
						}
					}
					try {
						new RewardSection(args[1]).removeBlock(args[2].toLowerCase(), data);
					} catch (InvalidRewardSectionException | BlockNotInListException e) {
						e.printStackTrace();
					}
				}
			} else if (args[0].equalsIgnoreCase("listblocks")) {
				try {
					Map<String,Boolean> blocks = new RewardSection(args[1]).getBlocksWithData();
					if (blocks.size() < 1) {
						sender.sendMessage(ChatColor.RED + "There are no blocks in that section.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The blocks that trigger rewards are:  ");
						boolean duplicates = false;
						for (Entry<String,Boolean> entry : blocks.entrySet()) {
							if (entry.getKey().contains("$")) duplicates = true;
							String block = entry.getKey().toLowerCase().replace("$", "");
							//block = block.replaceAll("$", ""); // strip prefixes generated to avoid duplicates
							if (entry.getValue() == null) {
								sender.sendMessage(ChatColor.GREEN + block);
							} else if (entry.getValue() == true) {
								sender.sendMessage(ChatColor.GREEN + block + ", fully grown.");
							} else {
								sender.sendMessage(ChatColor.GREEN + block + ", not fully grown");
							}
						}
						if (duplicates) {
							sender.sendMessage("There seems to be at least one duplicate item in the list.");
							sender.sendMessage("Fixing the duplicate item will help this plugin to run better.");
							sender.sendMessage("Note: This duplicate may take the form of *:true and *:false");
							sender.sendMessage("Instead, try just the block and it will cover both.");
						}
					}
				} catch (InvalidRewardSectionException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
					return true;
				}
			} else if (args[0].equalsIgnoreCase("addreward")) {
				String section = args[1];
				if (args.length == 2) {
					try {
						new RewardSection(section, true);
					} catch (InvalidRewardSectionException | RewardSectionAlreadyExistsException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Reward section creation complete, add some rewards with /cmr addreward " + section + " <reward> and some blocks with /cmr addblock " + section + " [block]");
				} else if (args.length == 3) {
					String name = args[2];
					try {
						new Reward(section, name, true);
					} catch (RewardAlreadyExistsException | InvalidRewardException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.YELLOW + "Reward creation complete, use /cmr addcommand " + section + " " + name + " <command> to add commands to this reward, and /cmr setchance " + section + " " + name + " <chance> to set the chance.");
				}
				//double chance = 0;
				//if (debug) {sender.sendMessage("name = " + name);}
				/*try {
					chance = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + args[3] + " is not a number!");
					return true;
				}*/
				//if (debug) {sender.sendMessage("chance = " + chance);}
				
			} else if (args[0].equalsIgnoreCase("removereward")) {
				if (args.length == 2) {
					try {
						new RewardSection(args[1]).delete();
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Reward section successfully removed.");
				} else if (args.length == 3) {
					try {
						new Reward(args[1], args[2]).delete();
					} catch (InvalidRewardSectionException | InvalidRewardException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Reward successfully removed!");
				}
			} else if (args[0].equalsIgnoreCase("listrewards")) {
				if (args.length == 1) {
					if (GlobalConfigManager.getRewardSections().size() == 0) {
						sender.sendMessage(ChatColor.RED + "There are no defined reward sections.  Add some with /cmr addreward");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The defined reward sections are:  " + GlobalConfigManager.getPrettyRewardSections());
					}
				} else if (args.length == 2) {
					try {
						if (new RewardSection(args[1]).getChildren().size() == 0) {
							sender.sendMessage(ChatColor.RED + "There are no defined rewards.");
						} else {
							sender.sendMessage(ChatColor.GREEN + "The defined rewards are:  " + new RewardSection(args[1]).getPrettyChildren());
						}
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(e.getMessage());
						return true;
					}
				}
			} else if (args[0].equalsIgnoreCase("addcommand")) {
				String rewardSection = args[1];
				String reward = args[2];
				String command = parseCommand(3, args);
				try {
					new Reward(rewardSection, reward).addCommand(command);
				} catch (InvalidRewardSectionException | InvalidRewardException | CommandAlreadyInListException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "Success!");
			} else if (args[0].equalsIgnoreCase("insertcommand")) {
				String rewardSection = args[1];
				String reward = args[2];
				int index;
				try {
					index = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
					return true;
				}
				String command = parseCommand(4, args);
				try {
					new Reward(rewardSection, reward).insertCommand(command, index);
				} catch (InvalidRewardSectionException | InvalidRewardException | ArrayIndexOutOfBoundsException | CommandAlreadyInListException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "Success!");
			} else if (args[0].equalsIgnoreCase("removecommand")) {
				if (args.length == 4) {
					String rewardSection = args[1];
					String reward = args[2];
					int index;
					try {
						index = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						try {
							new Reward(rewardSection, reward).removeCommand(args[3]);
						} catch (InvalidRewardSectionException | InvalidRewardException | ArrayIndexOutOfBoundsException | CommandNotInListException ex) {
							sender.sendMessage(ChatColor.RED + ex.getMessage());
							return true;
						}
						sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
						return true;
					}
					try {
						new Reward(rewardSection, reward).removeCommand(index);
					} catch (InvalidRewardSectionException | InvalidRewardException | ArrayIndexOutOfBoundsException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length > 4) {
					String rewardSection = args[1];
					String reward = args[2];
					String command = parseCommand(3, args);
					try {
						new Reward(rewardSection, reward).removeCommand(command);
					} catch (InvalidRewardSectionException | InvalidRewardException | CommandNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("listcommands")) {
				String rewardSection = args[1];
				String rewardName = args[2];
				try {
					Reward reward = new Reward(rewardSection, rewardName);
					if (reward.getCommands().size() == 0) {
						sender.sendMessage(ChatColor.RED + "There are no commands in that reward.  Add some with /cmr addcommand " + rewardSection + " " + reward + " <command>");
					} else {
						for (int i = 0; i < reward.getCommands().size(); i++) {
							sender.sendMessage(i + ": /" + reward.getCommands().get(i));
						}
					}
				} catch (InvalidRewardSectionException | InvalidRewardException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
					return true;
				}
			} else if (args[0].equalsIgnoreCase("addworld")) {
				if (args.length == 2) {
					String world = args[1];
					try {
						GlobalConfigManager.addGlobalAllowedWorld(world);
					} catch (WorldAlreadyInListException | InvalidWorldException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 3) {
					String rewardSection = args[1];
					String world = args[2];
					try {
						new RewardSection(rewardSection).addAllowedWorld(world);
					} catch (InvalidRewardSectionException | WorldAlreadyInListException | InvalidWorldException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("removeworld")) {
				if (args.length == 2) {
					String world = args[1];
					try {
						GlobalConfigManager.removeGlobalAllowedWorld(world);
					} catch (WorldNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 3) {
					String rewardSection = args[1];
					String world = args[2];
					try {
						new RewardSection(rewardSection).removeAllowedWorld(world);
					} catch (InvalidRewardSectionException | WorldNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("addcurrentworld")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
					return true;
				}
				if (args.length == 1) {
					try {
						GlobalConfigManager.addGlobalAllowedWorld(((Player)sender).getWorld().getName());
					} catch (WorldAlreadyInListException | InvalidWorldException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 2) {
					String rewardSection = args[1];
					try {
						new RewardSection(rewardSection).addAllowedWorld(((Player)sender).getWorld().getName());
					} catch (InvalidRewardSectionException | WorldAlreadyInListException | InvalidWorldException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("removecurrentworld")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
					return true;
				}
				if (args.length == 1) {
					try {
						GlobalConfigManager.removeGlobalAllowedWorld(((Player)sender).getWorld().getName());
					} catch (WorldNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 2) {
					String rewardSection = args[1];
					try {
						new RewardSection(rewardSection).removeAllowedWorld(((Player)sender).getWorld().getName());
					} catch (InvalidRewardSectionException | WorldNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("listworlds")) {
				if (args.length == 1) {
					if (GlobalConfigManager.getGlobalAllowedWorlds().size() == 0) {
						sender.sendMessage(ChatColor.RED + "There are no globally allowed worlds.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The globally allowed worlds are:  " + GlobalConfigManager.makePretty(GlobalConfigManager.getGlobalAllowedWorlds()));
					}
				} else if (args.length == 2) {
					RewardSection rs;
					try {
						rs = new RewardSection(args[1]);
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					if (rs.getAllowedWorlds().size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed worlds in this reward section.  The rewards world checker will use the global ones.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The allowed worlds are:  " + GlobalConfigManager.makePretty(rs.getAllowedWorlds()));
					}
				}
			} else if (args[0].equalsIgnoreCase("addregion")) {
				if (!usingWorldGuard()) {
					sender.sendMessage(ChatColor.RED + "Region commands are disabled because WorldGuard was not found.");
					return true;
				}
				if (args.length == 2) {
					String region = args[1];
					try {
						GlobalConfigManager.addGlobalAllowedRegion(region);
					} catch (RegionAlreadyInListException | InvalidRegionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 3) {
					String rewardSection = args[1];
					String region = args[2];
					try {
						new RewardSection(rewardSection).addAllowedRegion(region);
					} catch (InvalidRewardSectionException | RegionAlreadyInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("removeregion")) {
				if (!usingWorldGuard()) {
					sender.sendMessage(ChatColor.RED + "Region commands are disabled because WorldGuard was not found.");
					return true;
				}
				if (args.length == 2) {
					String region = args[1];
					try {
						GlobalConfigManager.removeGlobalAllowedRegion(region);
					} catch (RegionNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				} else if (args.length == 3) {
					String rewardSection = args[1];
					String region = args[2];
					try {
						new RewardSection(rewardSection).removeAllowedRegion(region);
					} catch (InvalidRewardSectionException | RegionNotInListException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Success!");
				}
			} else if (args[0].equalsIgnoreCase("listregions")) {
				if (!usingWorldGuard()) {
					sender.sendMessage(ChatColor.RED + "Region commands are disabled because WorldGuard was not found.");
					return true;
				}
				if (args.length == 1) {
					if (GlobalConfigManager.getGlobalAllowedRegions().size() == 0) {
						sender.sendMessage(ChatColor.RED + "There are no globally allowed regions.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The globally allowed regions are:  " + GlobalConfigManager.makePretty(GlobalConfigManager.getGlobalAllowedRegions()));
					}
				} else if (args.length == 2) {
					RewardSection rs;
					try {
						rs = new RewardSection(args[1]);
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					if (rs.getAllowedRegions().size() == 0) {
						sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed regions in this reward section.  The rewards region checker will use the global ones.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The allowed regions are:  " + GlobalConfigManager.makePretty(rs.getAllowedRegions()));
					}
				}
			} else if (args[0].equalsIgnoreCase("setsilktouchpolicy")) {
				SilkTouchRequirement req = SilkTouchRequirement.getByName(args[args.length - 1]);
				if (req == null) {
					sender.sendMessage(ChatColor.RED + "The policy " + args[args.length - 1] + " was not understood.  Please put REQUIRED, IGNORED, OR DISALLOWED.");
					return true;
				}
				if (args.length == 2) {
					GlobalConfigManager.setGlobalSilkTouchRequirement(req);
				} else if (args.length == 3) {
					String rewardSection = args[1];
					try {
						new RewardSection(rewardSection).setSilkTouchRequirement(req);
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
				} else if (args.length == 4) {
					String rewardSection = args[1];
					String reward = args[2];
					try {
						new Reward(rewardSection, reward).setSilkTouchRequirement(req);
					} catch (InvalidRewardSectionException | InvalidRewardException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
				}
				sender.sendMessage(ChatColor.GREEN + "Success!");
			} else if (args[0].equalsIgnoreCase("viewsilktouchpolicy")) {
				if (args.length == 1) {
					if (GlobalConfigManager.getGlobalSilkTouchRequirement() == null) {
						sender.sendMessage(ChatColor.RED + "There is no global silk touch requirement, any rewards or reward sections inheriting from the global setting will default to Ignored.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The global silk touch policy is " + GlobalConfigManager.getGlobalSilkTouchRequirement() + ".  Please note this will be overridden by this setting in any reward sections or rewards.");
					}
				} else if (args.length == 2) {
					String rewardSection = args[1];
					String result = null;
					try {
						RewardSection rs = new RewardSection(rewardSection); 
						if (rs.getSilkTouchRequirement() != null) {
							result = rs.getSilkTouchRequirement().getFriendlyName();
						}
					} catch (InvalidRewardSectionException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					if (result == null) {
						sender.sendMessage(ChatColor.RED + "This reward section has no defined silk touch requirement, it will be inherited from the global setting.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The silk touch policy for this reward section is " + result + ".");
					}
				} else if (args.length == 3) {
					String rewardSection = args[1];
					String rewardName = args[2];
					String result = null;
					try {
						Reward reward = new Reward(rewardSection, rewardName);
						if (reward.getSilkTouchRequirement() != null) {
							result = reward.getSilkTouchRequirement().getFriendlyName();
						}
					} catch (InvalidRewardSectionException | InvalidRewardException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					if (result == null) {
						sender.sendMessage(ChatColor.RED + "This reward has no defined silk touch requirement, it will be inherited from the parent reward section or the global setting.");
					} else {
						sender.sendMessage(ChatColor.GREEN + "The silk touch policy for this reward is " + result + ".");
					}
				}
			} else if (args[0].equalsIgnoreCase("chance")) {
				if (args.length == 3) {
					String rewardSection = args[1];
					String reward = args[2];
					double result;
					try {
						result = new Reward(rewardSection, reward).getChance();
					} catch (InvalidRewardSectionException | InvalidRewardException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true;
					}
					sender.sendMessage(ChatColor.GREEN + "Chance of triggering reward is: " + result + "%");
				} else if (args.length == 4) {
					if (sender.hasPermission(modifyChancePermission)) {
						String rewardSection = args[1];
						String reward = args[2];
						double chance;
						try {
							chance = Double.parseDouble(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Invalid number!");
							return true;
						}
						try {
							new Reward(rewardSection, reward).setChance(chance);
						} catch (InvalidRewardSectionException | InvalidRewardException e) {
							sender.sendMessage(ChatColor.RED + e.getMessage());
							return true;
						}
						sender.sendMessage(ChatColor.GREEN + "Success!");
					}
				}
			}
				/* else if (args[0].equalsIgnoreCase("confirm")) {
				if (sender.hasPermission(modifyRewardsPermission)) {
					if (waitingForConf.contains(sender.getName())) {
						int index = waitingForConf.indexOf(sender.getName());
						addReward(rewardsWaitingName.get(index), rewardsWaitingChance.get(index), true);
					} else {
						sender.sendMessage(ChatColor.RED + "You are not setting up a reward!");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("cancel")) {
				if (sender.hasPermission(modifyRewardsPermission)) {
					if (waitingForConf.contains(sender.getName())) {
						int index = waitingForConf.indexOf(sender.getName());
						waitingForConf.remove(index);
						rewardsWaitingName.remove(index);
						rewardsWaitingChance.remove(index);
						sender.sendMessage(ChatColor.YELLOW + "Reward setup cancelled.");
					} else {
						sender.sendMessage(ChatColor.RED + "You are not setting up a reward!");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			}*/ else {
				sender.sendMessage(ChatColor.RED + "Please do /cmr help for available commands.");
			}
			return true;
		}
	
		return false;	
		
		}
	}
