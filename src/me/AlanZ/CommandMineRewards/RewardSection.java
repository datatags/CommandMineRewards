package me.AlanZ.CommandMineRewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.AlanZ.CommandMineRewards.Exceptions.BlockAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.BlockNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidMaterialException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidWorldException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldNotInListException;
import me.AlanZ.CommandMineRewards.commands.silktouch.SilkTouchRequirement;
import me.AlanZ.CommandMineRewards.worldguard.WorldGuardManager;

public class RewardSection {
	private ConfigurationSection section;
	static CommandMineRewards cmr = null;
	// purpose of blocksCache is to not pass bad block values (i.e. invalid material, invalid growth state identifier, etc.)
	private static Map<String,List<String>> blocksCache = new HashMap<String,List<String>>();
	public RewardSection(String path, boolean createIfNotFound) {
		if (cmr == null) {
			throw new IllegalStateException("CMR instance has not been set!");
		}
		if (path.contains(".")) {
			throw new InvalidRewardSectionException("You cannot use periods in the reward section name!");
		}
		if (!cmr.getConfig().isConfigurationSection(path) && !createIfNotFound) { // if we couldn't find it easily and we're not supposed to create it,
			if (GlobalConfigManager.searchIgnoreCase(path, "") == null) { // search for it
				throw new InvalidRewardSectionException("Reward section " + path + " does not exist!"); // if we still couldn't find it, throw an exception
			} else {
				path = GlobalConfigManager.searchIgnoreCase(path, "");
			}
		} else if (cmr.getConfig().isConfigurationSection(path) && createIfNotFound){
			throw new InvalidRewardSectionException("Reward section " + path + " already exists!");
		}
		if (!cmr.getConfig().isConfigurationSection(path) && createIfNotFound) {
			cmr.getConfig().createSection(path);
			cmr.saveConfig();
		}
		section = cmr.getConfig().getConfigurationSection(path);
	}
	public RewardSection(String path) {
		this(path, false);
	}
	public RewardSection(ConfigurationSection section) {
		this.section = section;
	}
	protected static void init() {
		fillCache();
		registerPermissions();
	}
	protected static void fillCache() {
		for (RewardSection work : GlobalConfigManager.getRewardSections()) {
			if (!blocksCache.containsKey(work.getName())) { // if not cached
				blocksCache.put(work.getName(), work.validateBlocks(true)); // validate it and cache the result.
			}
		}
		// Probably don't need the following line because this method checks to see if it's already cached first before re-caching it. Plus it doesn't even check if it's already initialized :P
		//cmr.getLogger().severe("Received an attempt to initialize cache when it has already been initialized!"); // we didn't cache it, 
	}
	protected static void clearCache() {
		blocksCache = new HashMap<String,List<String>>();
	}
	private static void registerPermissions() {
		for (RewardSection section : GlobalConfigManager.getRewardSections()) {
			for (Reward reward : section.getChildren()) {
				String permission = "cmr.use." + section.getName() + "." + reward.getName();
				if (Bukkit.getPluginManager().getPermission(permission) == null) {
					debug("Adding permission " + permission);
					Bukkit.getPluginManager().addPermission(new Permission(permission));
				} else {
					cmr.getLogger().warning("Permission " + permission + " already exists! Was the server reloaded with /reload?");
				}
			}
		}
		
	}
	public List<String> getRawBlocks() {
		if (!blocksCache.containsKey(this.getName())) { // if not cached
			cmr.getLogger().warning("Reward section " + this.getName() + "'s block list was not cached.  Was it just created?");
			blocksCache.put(this.getName(), validateBlocks(false)); // validate it and cache the result.
		}
		return blocksCache.get(this.getName());
	}
	private List<String> validateBlocks(boolean log) { // don't log it except on the initial run
		List<String> blocks = new ArrayList<String>();
		for (String block : this.section.getStringList("blocks")) {
			debug("work = " + this.getName());
			String[] sections = block.split(":", 2); // item [0] = block; item [1] = data, if applicable
			if (Material.matchMaterial(sections[0]) == null || !Material.matchMaterial(sections[0]).isBlock()) {
				if (log) cmr.getLogger().severe("Reward section " + this.getName() + " has an invalid block in the blocks list:  " + sections[0] + ".  " + (GlobalConfigManager.removeInvalidValues() ? "Removing." : "Ignoring."));
				continue;
			}
			if (block.contains(":")) {
				if (!(Material.matchMaterial(sections[0]).createBlockData() instanceof Ageable)) {
					if (log) cmr.getLogger().severe("Reward section " + this.getName() + " has a growth identifier on a block that does not grow:  " + sections[0] + ".  " + (GlobalConfigManager.removeInvalidValues() ? "Removing." : "Ignoring."));
					continue;
				}
				if (!sections[1].equalsIgnoreCase("true") && !sections[1].equalsIgnoreCase("false")) {
					if (log) cmr.getLogger().severe("Reward section " + this.getName() + " has an invalid growth identifier:  " + sections[1] + ".  " + (GlobalConfigManager.removeInvalidValues() ? "Removing." : "Ignoring."));
					continue;
				}
			}
			blocks.add(block);
		}
		if (GlobalConfigManager.removeInvalidValues()) {
			this.section.set("blocks", blocks);
		}
		return blocks;
	}
	public List<String> getBlocks() {
		List<String> rv = new ArrayList<String>();
		for (String block : getRawBlocks()) {
			rv.add(block.split(":")[0].toLowerCase());
		}
		return rv;
	}
	public Map<String,Boolean> getBlocksWithData() {
		Map<String,Boolean> blocks = new HashMap<String,Boolean>();
		for (String block : getRawBlocks()) {
			if (!block.contains(":")) { // one element
				if (blocks.containsKey(block)) {
					cmr.getLogger().warning("Duplicate item (maybe different growth values) in blocks list of section " + this.getName() + ":  " + block + ".");
					String prefix = "";
					while (blocks.containsKey(prefix + block)) {
						prefix += "$";
					}
					blocks.put(prefix + block, null);
				} else {
					blocks.put(block, null);
				}
			} else { // two elements
				if (!(Material.matchMaterial(block.split(":")[0]).createBlockData() instanceof Ageable)) {
					cmr.getLogger().severe("Reward section " + this.getName() + " has a growth identifier on a non-growable block!");
					continue;
				}
				String blockStripped = block.split(":")[0];
				boolean data;
				if (block.split(":")[1].equalsIgnoreCase("true")) {
					data = true;
				} else if (block.split(":")[1].equalsIgnoreCase("false")) {
					data = false;
				} else {
					cmr.getLogger().severe("Reward section " + this.getName() + " has an invalid growth identifier under the block " + block.split(":")[0] + ".");
					continue;
				}
				if (blocks.containsKey(blockStripped)) {
					cmr.getLogger().warning("Duplicate item (maybe different growth values) in blocks list of section " + this.getName() + ":  " + block + " with data value " + block.split(":")[1] + ".");
					String prefix = "";
					while (blocks.containsKey(prefix + blockStripped)) {
						prefix += "$";
					}
					blocks.put(prefix + blockStripped, data);
				} else {
					blocks.put(blockStripped, data);
				}
			}
			
		}
		return blocks;
	}
	public void setBlocks(List<String> newBlocks) {
		this.section.set("blocks", newBlocks);
		cmr.saveConfig();
	}
	private void validateBlock(String block) throws InvalidMaterialException {
		Material strippedBlock = Material.matchMaterial(block.split(":")[0]);
		if (strippedBlock == null) {
			throw new InvalidMaterialException(block.split(":")[0] + " is not a valid block, it's not even a valid item.");
		}
		if (!strippedBlock.isBlock()) {
			throw new InvalidMaterialException(strippedBlock + " is not a block!");
		}
		if (strippedBlock == Material.AIR) {
			throw new InvalidMaterialException("You can't add air as a reward-triggering block!");
		}
		if (block.contains(":")) {
			String data = block.split(":")[1];
			if (!(strippedBlock.createBlockData() instanceof Ageable)) {
				throw new InvalidMaterialException("You can't add growth data to a non-growable block!");
			}
			if (!data.equalsIgnoreCase("true") && !data.equalsIgnoreCase("false")) {
				throw new InvalidMaterialException("Data value can only be either true or false!");
			}
		}
	}
	public void addBlock(String block) throws BlockAlreadyInListException, InvalidMaterialException {
		validateBlock(block);
		if (block.contains(":")) {
			String data = block.split(":")[1];
			block = block.split(":")[0];
			addBlock(block, data);
			return;
		}
		List<String> blocks = this.getRawBlocks();
		if (GlobalConfigManager.containsMatch(blocks, block + "(?::.+)?")) {
			throw new BlockAlreadyInListException("The block " + block + " is already handled by the reward section " + this.getName() + "!");
		}
		blocks.add(block);
		CMRBlockManager.addHandler(this, Material.matchMaterial(block));
		this.setBlocks(blocks);
	}
	public void addBlock(Material block) throws BlockAlreadyInListException, InvalidMaterialException {
		addBlock(block.toString());
	}
	public void addBlock(Material block, String data) throws BlockAlreadyInListException, InvalidMaterialException {
		addBlock(block.toString(), data);
	}
	public void addBlock(String block, String data) throws BlockAlreadyInListException, InvalidMaterialException {
		validateBlock(block + ":" + data); // in case it came from addBlock(Material, String) or a direct call. Plus it's just a command, doesn't hurt to validate twice.
		List<String> blocks = this.getRawBlocks();
		if (GlobalConfigManager.containsMatch(blocks, block + "(?::.+)?")) { // "(?:" means just a group, don't save result
			throw new BlockAlreadyInListException("The block " + block + " is already handled by the reward section " + this.getName() + "!");
		}
		blocks.add(block + ":" + data);
		CMRBlockManager.addCropHandler(this, Material.matchMaterial(block), Boolean.parseBoolean(data));
		this.setBlocks(blocks);
	}
	public void removeBlock(String block, boolean hasData) throws BlockNotInListException {
		List<String> blocks = this.getRawBlocks();
		if (GlobalConfigManager.removeIgnoreCase(blocks, block)) {
			blocks.remove(block);
			if (hasData) {
				CMRBlockManager.removeCropHandler(this, Material.matchMaterial(block.split(":")[0]), Boolean.parseBoolean(block.split(":")[1]));
			} else {
				CMRBlockManager.removeHandler(this, Material.matchMaterial(block));
			}
			this.setBlocks(blocks);
		} else {
			throw new BlockNotInListException("The block " + block + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public void removeBlock(String block, byte data) throws BlockNotInListException {
		removeBlock(block + ":" + data, true);
	}
	public void removeBlock(Material block) throws BlockNotInListException {
		removeBlock(block.toString(), false);
	}
	public List<String> getAllowedWorlds() {
		return this.section.getStringList("allowedWorlds");
	}
	public void setAllowedWorlds(List<String> newAllowedWorlds) {
		this.section.set("allowedWorlds", newAllowedWorlds);
		cmr.saveConfig();
	}
	public void addAllowedWorld(String world) throws WorldAlreadyInListException, InvalidWorldException {
		if (GlobalConfigManager.containsIgnoreCase(this.getAllowedWorlds(), world)) {
			throw new WorldAlreadyInListException("The world " + world + " is already handled by the reward section " + this.getName() + "!");
		}
		if (!GlobalConfigManager.isValidWorld(world)) {
			throw new InvalidWorldException("The world " + world + " does not exist!");
		}
		List<String> worlds = this.getAllowedWorlds();
		worlds.add(world);
		this.setAllowedWorlds(worlds);
	}
	public void removeAllowedWorld(String world) throws WorldNotInListException {
		List<String> worlds = this.getAllowedWorlds();
		if (GlobalConfigManager.removeIgnoreCase(worlds, world)) {
			this.setAllowedWorlds(worlds);
		} else {
			throw new WorldNotInListException("The world " + world + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public boolean isWorldAllowed(String world) {
		if (GlobalConfigManager.containsIgnoreCase(getAllowedWorlds(), world)) {
			return true;
		} else {
			return false;
		}
	}
	public List<String> getAllowedRegions() {
		return this.section.getStringList("allowedRegions");
	}
	public void setAllowedRegions(List<String> newAllowedRegions) {
		this.section.set("allowedRegions", newAllowedRegions);
		cmr.saveConfig();
	}
	public void addAllowedRegion(String region) throws RegionAlreadyInListException {
		if (GlobalConfigManager.containsIgnoreCase(this.getAllowedRegions(), region)) {
			throw new RegionAlreadyInListException("The region " + region + " is already handled by the reward section " + this.getName() + "!");
		}
		List<String> regions = this.getAllowedRegions();
		regions.add(region);
		this.setAllowedRegions(regions);
	}
	public void removeAllowedRegion(String region) throws RegionNotInListException {
		List<String> regions = this.getAllowedRegions();
		if (GlobalConfigManager.removeIgnoreCase(regions, region)) {
			this.setAllowedRegions(regions);
		} else {
			throw new RegionNotInListException("The region " + region + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public SilkTouchRequirement getSilkTouchRequirement() {
		return SilkTouchRequirement.getByName(this.section.getString("silkTouch"));
	}
	public void setSilkTouchRequirement(SilkTouchRequirement newRequirement) {
		this.section.set("silkTouch", newRequirement.toString());
		cmr.saveConfig();
	}
	public void delete() {
		for (Entry<String,Boolean> entry : getBlocksWithData().entrySet()) {
			CMRBlockManager.removeCropHandler(this, Material.matchMaterial(entry.getKey().replace("$", "")), entry.getValue());
		}
		cmr.getConfig().set(this.getPath(), null);
		cmr.saveConfig();
	}
	public String getName() {
		return this.section.getName();
	}
	public String getPath() {
		return this.section.getCurrentPath();
	}
	public List<String> getChildrenNames() {
		if (!this.section.isConfigurationSection("rewards")) {
			return new ArrayList<String>();
		}
		List<String> rv = new ArrayList<String>();
		for (String child : this.section.getConfigurationSection("rewards").getKeys(false)) {
			if (!this.section.isConfigurationSection("rewards." + child)) {
				continue;
			}
			rv.add(child);
		}
		return rv;
	}
	public List<Reward> getChildren() {
		if (!this.section.isConfigurationSection("rewards")) {
			return new ArrayList<Reward>();
		}
		List<Reward> rv = new ArrayList<Reward>();
		for (String child : this.section.getConfigurationSection("rewards").getKeys(false)) {
			if (!this.section.isConfigurationSection("rewards." + child)) {
				continue;
			}
			rv.add(new Reward(this, child, false));
		}
		return rv;
	}
	public String getPrettyChildren() {
		if (!this.section.isConfigurationSection("rewards")) {
			return "";
		}
		List<String> assembledList = new ArrayList<String>();
		for (String child : this.section.getConfigurationSection("rewards").getKeys(false)) {
			if (!this.section.isConfigurationSection("rewards." + child)) {
				continue;
			}
			assembledList.add(child);
		}
		return GlobalConfigManager.makePretty(assembledList);
	}
	public Reward getChild(String child) {
		if (!this.section.isConfigurationSection("rewards." + child)) {
			return null;
		}
		return new Reward(this.getName(), child);
	}
	private static void debug(String msg) {
		cmr.debug(msg);
	}
	public boolean isApplicable(Block block, Player player) {
		// block type is checked by CMRBlockHandler
		if (!GlobalConfigManager.isWorldAllowed(this, player.getWorld().getName())) {
			debug("Player was denied access to rewards in reward section because the reward section is not allowed in this world.");
			return false;
		}
		if (cmr.usingWorldGuard() && !WorldGuardManager.isAllowedInRegions(this, block)) {
			debug("Player was denied access to rewards in reward section because the reward section is not allowed in this region.");
			return false;
		}
		return true;
	}
	public void execute(Block block, Player player) {
		debug("Processing section " + this.getName());
		if (!isApplicable(block, player)) return;
		for (Reward reward : getChildren()) {
			if (reward.isApplicable(block, player)) {
				reward.execute(player);
			}
		}
	}
}
