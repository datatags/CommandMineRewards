package me.AlanZ;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.AlanZ.ItemInHand.ItemInHand;
import me.AlanZ.ItemInHand.ItemInHand_1_8;
import me.AlanZ.ItemInHand.ItemInHand_1_9;

public class CommandMineRewards extends JavaPlugin {
	
	public Permission reloadPermission = new Permission("cmr.reload");
	public Permission viewMultiplierPermission = new Permission("cmr.viewmultplier");
	public Permission modifyMultiplierPermission = new Permission("cmr.modifymultiplier");
	public Permission allRewardsPermission = new Permission("cmr.use.*");
	public Permission modifyBlocksPermission = new Permission("cmr.modifyblocks");
	public Permission viewBlocksPermission = new Permission("cmr.viewblocks");
	public Permission modifyRewardsPermission = new Permission("cmr.modifyrewards");
	public Permission viewRewardsPermission = new Permission("cmr.viewrewards");
	public Permission modifyRewardCommands = new Permission("cmr.modifycommands");
	public Permission viewRewardCommands = new Permission("cmr.viewcommands");
	public Permission debugPermission = new Permission("cmr.debug");
	public Permission viewChancePermission = new Permission("cmr.viewchance");
	public Permission modifyChancePermission = new Permission("cmr.modifychance");
	public String noPermissionMessage = ChatColor.RED + "You do not have permission to use this command!";
	
	PluginManager pm = getServer().getPluginManager();
	
	double multiplier;
	boolean debug;
	boolean survivalOnly;
	List<String> defBlocks = new ArrayList<String>();
	List<String> rewardsWithPermissions = new ArrayList<String>();
	/*List<String> waitingForConf = new ArrayList<String>();
	List<String> rewardsWaitingName = new ArrayList<String>();
	List<Double> rewardsWaitingChance = new ArrayList<Double>();*/
	boolean removeInvalidValues = false;
	ItemInHand iih = null;
	
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
		saveDefaultConfig();
		checkOldConfig();
		reload();
		new EventListener(this);
		if (!initItemInHand()) {
			getLogger().severe("Could not determine server version, plugin will not work properly!");
		}
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
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
		multiplier = this.getConfig().getDouble("multiplier");
		debug = this.getConfig().getBoolean("debug");
		survivalOnly = this.getConfig().getBoolean("survivalOnly");
		removeInvalidValues = this.getConfig().getBoolean("removeInvalidValues");
		getLogger().info("Checking for invalid items in blocks lists...");
		for (ConfigurationSection work : getConfigSections("")) {
			debug("work = " + work.getName());
			List<String> newBlocks = new ArrayList<String>();
			for (String block : work.getStringList("blocks")) {
				if (isMaterial(block)) {
					if (removeInvalidValues) {
						newBlocks.add(block);
					}
				}
			}
			for (ConfigurationSection section : getConfigSections(work.getConfigurationSection("rewards"))) {
				if (!rewardsWithPermissions.contains(work.getName() + "." + section.getName())) {
					getLogger().info("Adding permission cmr.use." + work.getName() + "." + section.getName());
					pm.addPermission(new Permission("cmr.use." + work.getName() + "." + section.getName()));
					rewardsWithPermissions.add(work.getName() + "." + section.getName());
				}
			}
			if (removeInvalidValues && newBlocks.size() != work.getStringList("blocks").size()) {
				work.set("blocks", newBlocks);
				this.getConfig().set(work.getName(), work);
			}
		}
		if (removeInvalidValues) {
			saveConfig();
		}
	}
	public void debug(String msg) {
		if (debug) {
			getLogger().info(msg);
		}
	}
	private boolean isMaterial(String mat) {
		//if (Material.valueOf(mat.toUpperCase()).isBlock()) {
		if (Material.matchMaterial(mat.split(":", 2)[0]) == null) {
			debug("Item " + mat + " of Blocks is not a valid material!  " + (removeInvalidValues ? "Removing." : "Ignoring."));
			return false;
		}
		if (Material.matchMaterial(mat.split(":", 2)[0]).isBlock()) {
			debug("defBlocks.get(i).isBlock() = true");
			return true;
		} else {
			getLogger().info("Item " + mat + " of Blocks is a material, but not a block!  " + (removeInvalidValues ? "Removing." : "Ignoring."));
			return false;
		}
	}
	public List<ConfigurationSection> blockHandled(Material mat, byte data) {
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
		List<String> list = new ArrayList<String>();;
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
	}
	public ItemStack getItemInHand(Player player) {
		return iih.getItemInHand(player);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("cmr")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "This server is running " + ChatColor.GOLD + "CommandMineRewards v" + this.getDescription().getVersion() + ChatColor.GREEN + "by AlanZ");
				sender.sendMessage(ChatColor.GREEN + "Do " + ChatColor.GOLD + "/cmr help" + ChatColor.GREEN + " for commands.");
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission(reloadPermission)) {
					reload();
					sender.sendMessage(ChatColor.YELLOW + "Multiplier, list of blocks, debug mode, and list of rewards has been reloaded.");
					if (debug) {
						sender.sendMessage(ChatColor.YELLOW + "Multiplier:  " + multiplier + ".  Debug:  " + debug + ".");
						sender.sendMessage(ChatColor.YELLOW + "Multiplier:  " + this.getConfig().getDouble("multiplier") + ".  Debug:  " + this.getConfig().getBoolean("debug") + ".");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to reload the config!");
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1 || args[1].equals("1")) {
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards" + ChatColor.GREEN + "----------");
					sender.sendMessage(ChatColor.GOLD + "/cmr: " + ChatColor.GREEN + "Displays information about the plugin.");
					sender.sendMessage(ChatColor.GOLD + "/cmr reload: " + ChatColor.GREEN + "Reload the config.");
					sender.sendMessage(ChatColor.GOLD + "/cmr multiplier [multiplier]: " + ChatColor.GREEN + "Change the reward chance multiplier.");
					sender.sendMessage(ChatColor.GOLD + "/cmr help: " + ChatColor.GREEN + "Display this message.");
					sender.sendMessage(ChatColor.GOLD + "/cmr addblock <rewardSection> [block]: " + ChatColor.GREEN + "Add a reward-triggering block.");
					sender.sendMessage(ChatColor.GOLD + "/cmr removeblock <rewardSection> [block]: " + ChatColor.GREEN + "Remove a reward-triggering block.");
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page 1 - /cmr help 2 for next page" + ChatColor.GREEN + "----------");
				} else if (args[1].equals("2")) {
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards - Page 2" + ChatColor.GREEN + "----------");
					sender.sendMessage(ChatColor.GOLD + "/cmr listblocks <rewardSection>: " + ChatColor.GREEN + "Lists the blocks that trigger rewards.");
					sender.sendMessage(ChatColor.GOLD + "/cmr addreward <rewardSection> <reward> <chance>: " + ChatColor.GREEN + "Add a reward.");
					sender.sendMessage(ChatColor.GOLD + "/cmr removereward <rewardSection> <reward>: " + ChatColor.GREEN + "Delete a reward.");
					sender.sendMessage(ChatColor.GOLD + "/cmr listrewards [rewardSection]: " + ChatColor.GREEN + "Lists all defined rewards.");
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page 2 - /cmr help 3 for next page" + ChatColor.GREEN + "----------");
				} else if (args[1].equals("3")) {
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards - Page 3" + ChatColor.GREEN + "----------");
					sender.sendMessage(ChatColor.GOLD + "/cmr addcommand <rewardSection> <reward> <command>: " + ChatColor.GREEN + "Add a command to the specified reward.");
					sender.sendMessage(ChatColor.GOLD + "/cmr insertcommand <rewardSection> <reward> <insert ID> <command>: " + ChatColor.GREEN + "Inserts a command at the specified ID, meaning the command will now be at the specified ID, shifting others down.");
					sender.sendMessage(ChatColor.GOLD + "/cmr removecommand <rewardSection> <reward> <id>: " + ChatColor.GREEN + "Removes the command with the ID specified in the specified reward.");
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page 3 - /cmr help 4 for next page" + ChatColor.GREEN + "----------");
				} else if (args[1].equals("4")) {
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards - Page 4" + ChatColor.GREEN + "----------");
					sender.sendMessage(ChatColor.GOLD + "/cmr listcommands <rewardSection> <reward>: " + ChatColor.GREEN + "Shows all commands that the specified reward runs when it is triggered, and their respective ID numbers.");
					sender.sendMessage(ChatColor.GOLD + "/cmr chance <rewardSection> <reward> [chance]: " + ChatColor.GREEN + "Change the chance that the given reward has of being triggered.");
					sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page 4" + ChatColor.GREEN + "----------");
				} else {
					sender.sendMessage(ChatColor.RED + "Please enter a valid page number from 1 to 4.");
				}
			} else if (args[0].equalsIgnoreCase("multiplier")) {
				if (sender.hasPermission(viewMultiplierPermission)) {
					if (args.length == 2) {
						if (sender.hasPermission(modifyMultiplierPermission)) {
							try {
								multiplier = Double.parseDouble(args[1]);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
								return false;
							}
							this.getConfig().set("multiplier", multiplier);
							saveConfig();
							sender.sendMessage(ChatColor.GREEN + "Multiplier successfully updated!  New multiplier:  " + multiplier);
						} else {
							sender.sendMessage(noPermissionMessage);
						}
					} else if (args.length == 1) {
						sender.sendMessage(ChatColor.YELLOW + "Current multiplier:  " + multiplier);
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr multiplier <multiplier>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			}/* else if (args[0].equalsIgnoreCase("debug")) {
				if (sender.hasPermission(debugPermission)) {
					sender.sendMessage(ChatColor.YELLOW + "debug:  " + debug + "  multiplier:  " + multiplier);
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			}*/ else if (args[0].equalsIgnoreCase("addblock")) {
				if (sender.hasPermission(modifyBlocksPermission)) {
					if (args.length >= 2 && args[1].contains(".")) {
						sender.sendMessage(ChatColor.RED + "You cannot use periods in the reward name!");
						return true;
					}
					if (args.length == 2) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							Material item = player.getInventory().getItemInMainHand().getType();
							if (item.isBlock() && item != Material.AIR) {
								int result = addBlock(args[1], item);
								if (result == 0) {
									player.sendMessage(ChatColor.GREEN + "Item " + item.toString().toLowerCase() + " added to blocks list.");
								} else if (result == 1) {
									player.sendMessage(ChatColor.RED + "That reward section does not exist!");
								} else if (result == 2) {
									player.sendMessage(ChatColor.RED + "That block is already in the blocks list of that reward!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block to add or manually specify one.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please specify a block to add to the list.");
						}
					} else if (args.length == 3) {
						if (Material.matchMaterial(args[2]) != null) {
							if (Material.matchMaterial(args[2]).isBlock()) {
								if (Material.matchMaterial(args[2]) != Material.AIR) {
									int result = addBlock(args[1], args[2].toLowerCase());
									if (result == 0) {  //Material.valueOf(args[2].toUpperCase()).toString().toLowerCase()
										sender.sendMessage(ChatColor.GREEN + args[2].toLowerCase() + " successfully added to blocks list!");
									} else if (result == 1) {
										sender.sendMessage(ChatColor.RED + "That reward section does not exist!");
									} else if (result == 2) {
										sender.sendMessage(ChatColor.RED + "That block is already in the blocks list!");
									}
								} else {
									sender.sendMessage(ChatColor.YELLOW + "You can't break air, silly!");
								}
							} else {
								sender.sendMessage(ChatColor.RED + args[2] + " is not a block!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + args[2] + " is not a block!  It's not even an item!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr addblock <rewardSection> [block]");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("removeblock")) {
				if (sender.hasPermission(modifyBlocksPermission)) {
					if (args.length >= 2 && args[1].contains(".")) {
						sender.sendMessage(ChatColor.RED + "You cannot use periods in the reward name!");
						return true;
					}
					if (args.length == 2) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							Material item = player.getInventory().getItemInMainHand().getType();
							if (item.isBlock() && item != Material.AIR) {
								int returnValue = removeBlock(args[1], item); 
								if (returnValue == 0) {
									player.sendMessage(ChatColor.GREEN + "The item " + item.toString().toLowerCase() + " was successfully removed.");
								} else if (returnValue == 1) {
									player.sendMessage(ChatColor.RED + "That reward section does not exist!");
								} else if (returnValue == 2) {
									player.sendMessage(ChatColor.RED + "The block " + item.toString().toLowerCase() + " was not in the list of reward-triggering blocks!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block or manually specify a block to remove.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please specify a block to remove from the list.");
						}
						return true;
					} else if (args.length == 3) {
						int returnValue = removeBlock(args[1], args[2]);
						if (returnValue == 0) {
							sender.sendMessage(ChatColor.GREEN + "The item " + args[2].toLowerCase() + " was successfully removed.");
						} else if (returnValue == 1) {
							sender.sendMessage(ChatColor.RED + "That reward section does not exist!");
						} else if (returnValue == 2) {
							sender.sendMessage(ChatColor.RED + "The block " + args[2].toString().toLowerCase() + " was not in the list of reward-triggering blocks!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr removeblock <rewardSection> [block]");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("listblocks")) {
				if (sender.hasPermission(viewBlocksPermission)) {
					if (args.length == 2) {
						String blocks = listBlocks(args[1]);
						if (blocks == null) {
							sender.sendMessage(ChatColor.RED + "There is no reward section named that.");
						} else if (blocks.equals("")) {
							sender.sendMessage(ChatColor.RED + "There are no blocks in that section.");
						} else {
							sender.sendMessage(ChatColor.GREEN + "The blocks that trigger rewards are:  " + blocks);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr listblocks <rewardSection>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("addreward")) {
				if (sender.hasPermission(modifyRewardsPermission)) {
					if (args.length == 3) {
						String section = args[1];
						String name = args[2];
						//double chance = 0;
						if (debug) {sender.sendMessage("name = " + name);}
						/*try {
							chance = Double.parseDouble(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a number!");
							return true;
						}*/
						//if (debug) {sender.sendMessage("chance = " + chance);}
						if (!addReward(section, name)) {
							sender.sendMessage("Reward already exists.");
						} else {
							saveConfig();
							sender.sendMessage(ChatColor.YELLOW + "Reward setup complete, use /cmr addcommand " + section + " " + name + " <command> to add commands to this reward, and /cmr setchance " + section + " " + name + " <chance> to set the chance.");
						}
					} else if (args.length == 4) {
						String section = args[1];
						String name = args[2];
						double chance = 0;
						if (debug) {sender.sendMessage("name = " + name);}
						try {
							chance = Double.parseDouble(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a number!");
							return true;
						}
						if (debug) {sender.sendMessage("chance = " + chance);}
						if (!addReward(section, name, chance)) {
							sender.sendMessage("Reward already exists.");
						} else {
							sender.sendMessage(ChatColor.YELLOW + "Reward setup complete, use /cmr addcommand " + section + " " + name + " <command> to add commands to this reward.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr addreward <rewardSection> <rewardName> [chance]");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("removereward")) {
				if (sender.hasPermission(modifyRewardsPermission)) {
					if (args.length == 2) {
						if (removeReward(args[1], null)) {
							sender.sendMessage(ChatColor.GREEN + "Reward section successfully removed.");
						} else {
							sender.sendMessage(ChatColor.RED + "That reward section does not exist!");
						}
					} else if (args.length == 3) {
						if (removeReward(args[1], args[2])) {
							sender.sendMessage(ChatColor.GREEN + "Reward successfully removed!");
						} else {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						}
					} else if (args.length > 3) {
						sender.sendMessage(ChatColor.RED + "Too many args!");
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr removereward <rewardSection> <reward>");
					} else if (args.length < 2) {
						sender.sendMessage(ChatColor.RED + "Not enough args!");
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr removereward <rewardSection> <reward>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("listrewards")) {
				if (sender.hasPermission(viewRewardsPermission)) {
					if (args.length == 1) {
						if (listRewards("").length() == 0) {
							sender.sendMessage(ChatColor.RED + "There are no defined reward sections.  Add some with /cmr addreward");
						} else {
							sender.sendMessage(ChatColor.GREEN + "The defined reward sections are:  " + listRewards(""));
						}
					} else if (args.length == 2) {
						if (listRewards(args[1]).length() == 0) {
							sender.sendMessage(ChatColor.RED + "There are no defined rewards.");
						} else {
							sender.sendMessage(ChatColor.GREEN + "The defined rewards are:  " + listRewards(args[1]));
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr listrewards [rewardSection]");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("addcommand")) {
				if (sender.hasPermission(modifyRewardCommands)) {
					if (args.length >= 4) {
						String rewardSection = args[1];
						String reward = args[2];
						String command = parseCommand(3, args);
						if (addCommand(args[1], args[2], command)) {
							sender.sendMessage(ChatColor.GREEN + "Success!");
						} else {
							sender.sendMessage(ChatColor.RED + "The reward " + reward + " in the section " + rewardSection + "does not exist!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr addcommand <rewardSection> <reward> <command>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("insertcommand")) {
				if (sender.hasPermission(modifyRewardCommands)) {
					if (args.length >= 5) {
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
						int returnValue = insertCommand(rewardSection, reward, index, command);
						if (returnValue == 0) {
							sender.sendMessage(ChatColor.GREEN + "Success!");
						} else if (returnValue == 1){
							sender.sendMessage(ChatColor.RED + "The specified reward or reward section does not exist.");
						} else if (returnValue == 2) {
							sender.sendMessage(ChatColor.RED + "Can't insert with an ID not adjacent to others in the list.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr insertcommand <rewardSection> <reward> <insert index> <command>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("removecommand")) {
				if (sender.hasPermission(modifyRewardCommands)) {
					if (args.length == 4) {
						String rewardSection = args[1];
						String reward = args[2];
						int index;
						try {
							index = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							int returnValue = removeCommand(rewardSection, reward, args[3]);
							if (returnValue == 0) {
								sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
							} else if (returnValue == 1) {
								sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
							} else if (returnValue == 2) {
								sender.sendMessage(ChatColor.RED + "That command is not in the list of commands for that reward!/");
							}
							return true;
						}
						int returnValue = removeCommand(rewardSection, reward, index);
						if (returnValue == 0) {
							sender.sendMessage(ChatColor.GREEN + "Success!");
						} else if (returnValue == 1) {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						} else if (returnValue == 2) {
							sender.sendMessage(ChatColor.RED + "There is no command with the ID " + index + ".  Check IDs with /cmr listcommands " + rewardSection + " " + reward);
						}
					} else if (args.length > 4) {
						String rewardSection = args[1];
						String reward = args[2];
						int returnValue = removeCommand(rewardSection, reward, parseCommand(3, args));
						if (returnValue == 0) {
							sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
						} else if (returnValue == 1) {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						} else if (returnValue == 2) {
							sender.sendMessage(ChatColor.RED + "That command is not in the list of commands for that reward!/");
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr removecommand <rewardSection> <reward> <command ID> OR <command>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("listcommands")) {
				if (sender.hasPermission(viewRewardCommands)) {
					if (args.length == 3) {
						String rewardSection = args[1];
						String reward = args[2];
						if (listCommands(rewardSection, reward).size() == 0) {
							sender.sendMessage(ChatColor.RED + "There are no commands in that reward.  Add some with /cmr addcommand " + rewardSection + " " + reward + " <command>");
						}
						for (String command : listCommands(rewardSection, reward)) {
							sender.sendMessage(command);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr listcommands <rewardSection> <reward>");
					}
				}
			} else if (args[0].equalsIgnoreCase("getchance")) {
				if (sender.hasPermission(viewChancePermission)) {
					if (args.length == 3) {
						String rewardChance = args[1];
						String reward = args[2];
						double result = getChance(rewardChance, reward);
						if (result >= 0) {
							sender.sendMessage(ChatColor.GREEN + "Chance of triggering reward is:  " + result);
						} else if (result == -1) {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						} else if (result == -2) {
							sender.sendMessage(ChatColor.RED + "That reward has an invalid or nonexistant chance value.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr getchance <rewardSection> <reward>");
					}
				} else {
					sender.sendMessage(noPermissionMessage);
				}
			} else if (args[0].equalsIgnoreCase("chance")) {
				if (sender.hasPermission(modifyChancePermission)) {
					if (args.length == 3) {
						String rewardChance = args[1];
						String reward = args[2];
						double result = getChance(rewardChance, reward);
						if (result >= 0) {
							sender.sendMessage(ChatColor.GREEN + "Chance of triggering reward is:  " + result);
							if (multiplier != 1) {
								sender.sendMessage(ChatColor.GREEN + "Taking the current multiplier of " + multiplier + " into account, that means there is a " + result * multiplier + "% chance of triggering the reward.");
							}
						} else if (result == -1) {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						} else if (result == -2) {
							sender.sendMessage(ChatColor.RED + "That reward has an invalid or nonexistant chance value.");
						}
					} else if (args.length == 4) {
						String rewardSection = args[1];
						String reward = args[2];
						double chance;
						try {
							chance = Double.parseDouble(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Invalid number!");
							return true;
						}
						if (setChance(rewardSection, reward, chance)) {
							sender.sendMessage(ChatColor.GREEN + "Success!");
						} else {
							sender.sendMessage(ChatColor.RED + "That reward or reward section does not exist!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Proper usage:  /cmr chance <rewardSection> <reward> [chance]");
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
