package me.AlanZ.CommandMineRewards;

import java.util.ArrayList;
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
	
	//double multiplier;
	//private boolean debug;
	//boolean survivalOnly;
	List<String> defBlocks = new ArrayList<String>();
	List<String> rewardsWithPermissions = new ArrayList<String>();
	/*List<String> waitingForConf = new ArrayList<String>();
	List<String> rewardsWaitingName = new ArrayList<String>();
	List<Double> rewardsWaitingChance = new ArrayList<Double>();*/
	boolean removeInvalidValues = false;
	private ItemInHand iih = null;
	private boolean worldGuardLoaded = false;
	private Map<String,Permission> commandPermissions = new HashMap<String,Permission>();
	private Map<String,String> commandDescription = new LinkedHashMap<String,String>();
	private Map<String,String> commandUsage = new LinkedHashMap<String,String>();
	private Map<String,Integer> commandMinArgs = new HashMap<String,Integer>();
	private Map<String,Integer> commandMaxArgs = new HashMap<String,Integer>();
	private final int helpPages = 7; // number of pages in help command
	
	private void initCommands() {
		commandDescription.put("reload", "Reload the config");
		commandDescription.put("multiplier", "Change the reward chance multiplier");
		commandDescription.put("help", "Display this message");
		commandDescription.put("addblock", "Add a reward-triggering block");
		commandDescription.put("removeblock", "Remove a reward-triggering block");
		commandDescription.put("listblocks", "Lists the blocks that trigger rewards");
		commandDescription.put("addreward", "Add a reward");
		commandDescription.put("removereward", "Delete a reward");
		commandDescription.put("listrewards", "Lists all defined rewards");
		commandDescription.put("addcommand", "Add a command to the specified reward");
		commandDescription.put("insertcommand", "Inserts a command at the specified ID, meaning the command will now be at the specified ID, shifting others down");
		commandDescription.put("removecommand", "Removes the command with the ID specified in the specified reward");
		commandDescription.put("listcommands", "Shows all commands that the specified reward runs when it is triggered, and their respective ID numbers");
		commandDescription.put("addworld", "Adds a world to the list of allowed worlds of a reward section, or globally.");
		commandDescription.put("addcurrentworld", "Adds the world you are currently in to the list of allowed worlds of a reward section, or globally.");
		commandDescription.put("removeworld", "Removes a world from the list of allowed worlds of a reward section, or globally.");
		commandDescription.put("removecurrentworld", "Removes the world you are currently in from the list of allowed worlds of a reward section, or globally.");
		commandDescription.put("listworlds", "Shows the list of allowed worlds of a reward section, or globally.");
		commandDescription.put("addregion", "Adds a region to the list of allowed regions of a reward section, or globally.");
		commandDescription.put("removeregion", "Removes a region from the list of allowed regions of a reward section, or globally.");
		commandDescription.put("listregions", "Shows the list of allowed regions of a reward section, or globally.");
		commandDescription.put("setsilktouchpolicy", "Changes the silk touch requirement or forbidden-ment globally, per-reward-section, or per-reward.");
		commandDescription.put("viewsilktouchpolicy", "Observes the silk touch policy of a reward, reward section, or globally.");
		commandDescription.put("chance", "Change the chance that the given reward has of being triggered");
		
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
			getLogger().info("You seem to be running < 1.9");
			iih = new ItemInHand_1_8();
		} else {
			getLogger().info("You seem to be running >= 1.9");
			iih = new ItemInHand_1_9();
		}
		return true;
	}
	@Override
	public void onDisable() {
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
		//multiplier = this.getConfig().getDouble("multiplier");
		//debug = this.getConfig().getBoolean("debug");
		//survivalOnly = this.getConfig().getBoolean("survivalOnly");
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
		if (removeInvalidValues) {
			saveConfig();
		}
	}
	public void debug(String msg) {
		if (GlobalConfigManager.getDebug()) {
			getLogger().info(msg);
		}
	}
	private boolean validateCommand(String[] args, CommandSender sender) {
		aliasHelper(args);
		String command = args[0];
		if (!commandDescription.containsKey(command.toLowerCase())) {
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
	/*public List<ConfigurationSection> blockHandled(Material mat, byte data) {
		List<ConfigurationSection> list = new ArrayList<ConfigurationSection>();
		for (ConfigurationSection section : getConfigSections("")) {
			if (!section.isList("blocks")) {
				debug("No blocks list in " + section.getName());
				continue;
			}
			for (String block : section.getStringList("blocks")) {
				String[] segments = block.split(":", 2);
				if (segments.length == 1) {
					if (mat == Material.matchMaterial(segments[0])) {
						list.add(section);
					} else {
						//debug(mat.toString() + " != " + Material.matchMaterial(segments[0]).toString());
					}
				} else { // must have two elements
					if (mat == Material.matchMaterial(segments[0]) && data == Byte.parseByte(segments[1])) {
						list.add(section);
					} else {
						//debug(mat.toString() + " != " + Material.matchMaterial(segments[0]).toString() + " and/or " + data + " != " + Byte.parseByte(segments[1]));
					}
				}
			}
		}
		return list;
	}
	public List<RewardSection> getRewardSections() {
		List<RewardSection> rv = new ArrayList<RewardSection>();
		for (ConfigurationSection section : getConfigSections("")) {
			rv.add(new RewardSection(section.getName(), false));
		}
		return rv;
	}
	public int addBlock(String rewardSection, String block) {
		if (!this.getConfig().isConfigurationSection(rewardSection)) {
			return 1;
		}
		List<String> blocks = this.getConfig().getConfigurationSection(rewardSection).getStringList("blocks");
		if (blocks.contains(block)) {
			return 2;
		}
		blocks.add(block);
		this.getConfig().set(rewardSection + ".blocks", blocks);
		saveConfig();
		return 0;
	}
	public int removeBlock(String rewardSection, String block) {
		if (!this.getConfig().isConfigurationSection(rewardSection)) {
			return 1;
		}
		List<String> blocks = this.getConfig().getConfigurationSection(rewardSection).getStringList("blocks");
		if (!blocks.remove(block)) {
			return 2;
		}
		this.getConfig().set(rewardSection + ".blocks", blocks);
		saveConfig();
		return 0;
	}
	private int addBlock(String rewardSection, Material block) {
		return addBlock(rewardSection, block.toString().toLowerCase());
	}
	private int removeBlock(String rewardSection, Material block) {
		return removeBlock(rewardSection, block.toString().toLowerCase());
	}
	public boolean addReward(String sectionName, String name, double chance) {
		if (!addReward(sectionName, name)) {
			return false;
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection(sectionName);
		section.getConfigurationSection("rewards." + name).set("chance", chance);
		this.getConfig().set(sectionName, section);
		saveConfig();
		//this.getConfig().set("Rewards." + name + ".commands", commands);
		return true;
	}
	public boolean addReward(String sectionName, String name) {
		if (!this.getConfig().isConfigurationSection(sectionName)) {
			this.getConfig().createSection(sectionName);
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection(sectionName);
		if (section.isConfigurationSection(name)) {
			return false;
		}
		if (!section.isConfigurationSection("rewards")) {
			section.createSection("rewards");
		}
		section.createSection("rewards." + name);
		return true;
	}
	public boolean removeReward(String sectionName, String name) {
		if (!this.getConfig().isConfigurationSection(sectionName)) {
			return false;
		}
		if (name == null) {
			this.getConfig().set(sectionName, null);
		} else {
			if (!this.getConfig().getConfigurationSection(sectionName).getConfigurationSection("rewards").isConfigurationSection(name)) {
				return false;
			}
			this.getConfig().getConfigurationSection(sectionName).set("rewards." + name, null);
		}
		saveConfig();
		return true;
	}
	public boolean addCommand(String rewardSection, String reward, String command) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return false;
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").getConfigurationSection(reward);
		List<String> cmds = section.getStringList("commands");
		cmds.add(command);
		section.set("commands", cmds);
		this.getConfig().set(rewardSection + ".rewards." + reward, section);
		saveConfig();
		return true;
	}
	public int insertCommand(String rewardSection, String reward, int index, String command ) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return 1;
		}
		ConfigurationSection work = this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").getConfigurationSection(reward);
		List<String> commands = work.getStringList("commands");
		if (index > commands.size()) {
			return 2;
		}
		//command = command.substring(0, command.length() - 1); // strip trailing space off command. done automatically now
		commands.add(index, command);
		work.set("commands", commands);
		this.getConfig().set(rewardSection + ".rewards." + reward, work);
		saveConfig();
		return 0;
	}
	public int removeCommand(String rewardSection, String reward, String command) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return 1;
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").getConfigurationSection(reward);
		List<String> cmds = section.getStringList("commands");
		for (String cmd : cmds) {
			if (cmd.equalsIgnoreCase(command)) {
				cmds.remove(cmd);
				section.set("commands", cmds);
				this.getConfig().set(rewardSection + ".rewards." + reward, section);
				saveConfig();
				return 0;
			}
		}
		return 2;
	}
	public int removeCommand(String rewardSection, String reward, int index) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return 1;
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").getConfigurationSection(reward);
		List<String> cmds = section.getStringList("commands");
		if (cmds.size() > index) {
			cmds.remove(index);
			section.set("commands", cmds);
			this.getConfig().set(rewardSection + ".rewards." + reward, section);
			saveConfig();
			return 0;
		} else {
			return 2;
		}
	}
	public String listBlocks(String rewardSection) {
		if (!this.getConfig().isConfigurationSection(rewardSection)) {
			return null;
		}
		List<String> blocks = this.getConfig().getConfigurationSection(rewardSection).getStringList("blocks");
		if (blocks == null || blocks.size() == 0) {
			return "";
		}
		return listToEnglish(blocks);
	}
	public String listRewards(String rewardSection) {
		List<String> rewards = new ArrayList<String>();
		for (ConfigurationSection section : getConfigSections((rewardSection == "" ? "" : rewardSection + ".rewards"))) {
			rewards.add(section.getName());
		}
		return listToEnglish(rewards);
	}
	private List<String> listCommands(String rewardSection, String reward) {
		List<String> list = new ArrayList<String>();
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().isConfigurationSection(rewardSection + ".rewards." + reward)) {
			list.add(ChatColor.RED + "No commands.");
			return list;
		}
		List<String> commands = this.getConfig().getStringList(rewardSection + ".rewards." + reward + ".commands");
		for (int i = 0; i < commands.size(); i++) {
			list.add(ChatColor.GREEN.toString() + i + ":  /" + commands.get(i));
		}
		return list;
	}
	private double getChance(String rewardSection, String reward) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return -1;
		}
		if (!this.getConfig().isDouble(rewardSection + ".rewards." + reward + ".chance")) {
			return -2;
		}
		return this.getConfig().getDouble(rewardSection + ".rewards." + reward + ".chance");
	}
	private boolean setChance(String rewardSection, String reward, double chance) {
		if (!this.getConfig().isConfigurationSection(rewardSection) || !this.getConfig().getConfigurationSection(rewardSection).getConfigurationSection("rewards").isConfigurationSection(reward)) {
			return false;
		}
		this.getConfig().set(rewardSection + ".rewards." + reward + ".chance", chance);
		saveConfig();
		return true;
	}
	private String listToEnglish(List<String> list) {
		String assembledList = "";
		for (String item : list) {
			if (item.equals(list.get(list.size() - 1))) { // if item is the last item in the list...
				if (list.size() > 1) {
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
	private SilkTouchRequirement getSilkTouchStatus(ConfigurationSection rewardSection, ConfigurationSection reward) {
		if (reward.isString("silkTouch")) {
			String value = reward.getString("silkTouch");
			if (SilkTouchRequirement.getByName(value) == null) {
				getLogger().warning("Could not parse silkTouch value in reward section '" + rewardSection.getName() + "' and reward '" + reward.getName() + "'.");
			} else {
				return SilkTouchRequirement.getByName(value);
			}
		}
		if (rewardSection.isString("silkTouch")) {
			String value = rewardSection.getString("silkTouch");
			if (SilkTouchRequirement.getByName(value) == null) {
				getLogger().warning("Could not parse silkTouch value in reward section '" + rewardSection.getName() + "'.");
			} else {
				return SilkTouchRequirement.getByName(value);
			}
		}
		if (this.getConfig().isString("silkTouch")) {
			String value = this.getConfig().getString("silkTouch");
			if (SilkTouchRequirement.getByName(value) == null) {
				getLogger().warning("Could not parse global silkTouch value.");
			} else {
				return SilkTouchRequirement.getByName(value);
			}
		}
		return null;
	}
	public boolean isSilkTouchAllowed(ConfigurationSection rewardSection, ConfigurationSection reward, boolean silkTouch) {
		SilkTouchRequirement requirement = getSilkTouchStatus(rewardSection, reward);
		if (requirement == null || requirement == SilkTouchRequirement.IGNORED) {
			return true;
		}
		if (requirement == SilkTouchRequirement.REQUIRED && silkTouch) {
			return true;
		}
		if (requirement == SilkTouchRequirement.DISALLOWED && !silkTouch) {
			return true;
		}
		return false;
	}
	public boolean isWorldAllowed(ConfigurationSection rewardSection, String worldName) {
		if (rewardSection.isList("allowedWorlds")) {
			for (String allowed : rewardSection.getStringList("allowedWorlds")) {
				if (allowed.equalsIgnoreCase(worldName) || allowed.equals("*")) {
					return true;
				}
			}
		} else if (rewardSection.isString("allowedWorlds")) { 
			String allowed = rewardSection.getString("allowedWorlds");
			if (allowed.equalsIgnoreCase(worldName) || allowed.equals("*")) {
				return true;
			}
		} else if (this.getConfig().isList("allowedWorlds")) {
			for (String allowed : this.getConfig().getStringList("allowedWorlds")) {
				if (allowed.equalsIgnoreCase(worldName) || allowed.equals("*")) {
					return true;
				}
			}
		} else if (this.getConfig().isString("allowedWorlds")) { 
			String allowed = rewardSection.getString("allowedWorlds");
			if (allowed.equalsIgnoreCase(worldName) || allowed.equals("*")) {
				return true;
			}
		} else {
			debug("Couldn't find allowed worlds list globally or in reward section " + rewardSection.getName());
			return true;
		}
		return false;
	}*/
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
			getLogger().severe("Couldn't find usage information for command /cmr " + command);
			sender.sendMessage(internalErrorMessage);
			return;
		}
		sender.sendMessage(ChatColor.GOLD + "/cmr " + command + (commandUsage.get(command).equals("") ? "" : (" " + commandUsage.get(command))) + ": " + ChatColor.GREEN + commandDescription.get(command));
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
				} else {
					sender.sendMessage(ChatColor.RED + "Please enter a valid page number from 1 to " + helpPages + ".");
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
