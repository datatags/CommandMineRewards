package me.Datatags.CommandMineRewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.Datatags.CommandMineRewards.Exceptions.BlockAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.BlockNotInListException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidMaterialException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidAreaException;
import me.Datatags.CommandMineRewards.Exceptions.AreaAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.AreaNotInListException;
import me.Datatags.CommandMineRewards.Exceptions.RewardSectionAlreadyExistsException;
import me.Datatags.CommandMineRewards.worldguard.WorldGuardManager;

public class RewardSection {
	private ConfigurationSection section;
	private CommandMineRewards cmr;
	private GlobalConfigManager gcm;
	private CMRBlockManager cbm;
	// purpose of blocksCache is to not pass bad block values (i.e. invalid material, invalid growth state identifier, etc.)
	private List<String> blocksCache = null;
	private List<Reward> children = null;
	public RewardSection(String path, boolean create) {
		init();
		if (path.contains(".")) {
			throw new InvalidRewardSectionException("You cannot use periods in the reward section name!");
		}
		if (!gcm.getRewardsConfig().isConfigurationSection(path) && !create) { // if we couldn't find it easily and we're not supposed to create it,
			if (gcm.searchIgnoreCase(path, "") == null) { // search for it
				throw new InvalidRewardSectionException("Reward section " + path + " does not exist!"); // if we still couldn't find it, throw an exception
			}
			path = gcm.searchIgnoreCase(path, "");
		} else if (gcm.getRewardsConfig().isConfigurationSection(path) && create){
			throw new RewardSectionAlreadyExistsException("Reward section " + path + " already exists!");
		}
		if (!gcm.getRewardsConfig().isConfigurationSection(path) && create) {
			section = gcm.getRewardsConfig().createSection(path);
			gcm.saveRewardsConfig();
			cbm.loadSection(getName());
		} else {
			section = gcm.getRewardsConfig().getConfigurationSection(path);
		}
		postinit();
	}
	public RewardSection(String path) {
		this(path, false);
	}
	private void init() {
		cmr = CommandMineRewards.getInstance();
		gcm = GlobalConfigManager.getInstance();
		cbm = CMRBlockManager.getInstance();
	}
	private void postinit() {
		blocksCache = this.validateBlocks(); // cache valid blocks
		// cache children
		if (this.section.isConfigurationSection("rewards")) {
			List<Reward> rv = new ArrayList<Reward>();
			for (String child : getChildrenNames()) {
				rv.add(new Reward(this, child, false));
			}
			children = rv;
		} else {
			children = new ArrayList<Reward>();
		}
		registerPermissions();
	}
	private void registerPermissions() {
		CommandMineRewards cmr = CommandMineRewards.getInstance();
		for (Reward reward : this.getChildren()) {
			String permission = "cmr.use." + section.getName() + "." + reward.getName();
			if (Bukkit.getPluginManager().getPermission(permission) == null) {
				cmr.debug("Adding permission " + permission);
				Bukkit.getPluginManager().addPermission(new Permission(permission));
			}
		}
		
	}
	public List<String> getRawBlocks() {
		if (blocksCache == null) { // if not cached
			cmr.warning("Reward section " + this.getName() + "'s block list was not cached.  Was it just created?");
			blocksCache = validateBlocks(); // validate it and cache the result.
		}
		return blocksCache;
	}
	private List<String> validateBlocks() {
		boolean log = !cmr.isPluginReady(); // don't log it except on the initial run
		List<String> blocks = new ArrayList<String>();
		for (String block : this.section.getStringList("blocks")) {
			String[] sections = block.split(":", 2); // item [0] = block; item [1] = data, if applicable
			if (Material.matchMaterial(sections[0]) == null || !Material.matchMaterial(sections[0]).isBlock()) {
				if (log) cmr.error("Reward section " + this.getName() + " has an invalid block in the blocks list:  " + sections[0] + ".  " + (gcm.removeInvalidValues() ? "Removing." : "Ignoring."));
				continue;
			}
			if (block.contains(":")) {
				if (!cbm.getStateManager().canHaveData(Material.matchMaterial(sections[0]))) {
					if (log) cmr.error("Reward section " + this.getName() + " has a growth identifier on a block that does not grow:  " + sections[0] + ".  " + (gcm.removeInvalidValues() ? "Removing." : "Ignoring."));
					continue;
				}
				if (!sections[1].equalsIgnoreCase("true") && !sections[1].equalsIgnoreCase("false")) {
					if (log) cmr.error("Reward section " + this.getName() + " has an invalid growth identifier:  " + sections[1] + ".  " + (gcm.removeInvalidValues() ? "Removing." : "Ignoring."));
					continue;
				}
			}
			debug("Adding block " + block + " to section " + this.getName());
			blocks.add(block);
		}
		if (gcm.removeInvalidValues()) {
			set("blocks", blocks, true);
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
					cmr.warning("Duplicate item (maybe different growth values) in blocks list of section " + this.getName() + ":  " + block + ".");
					String prefix = "";
					while (blocks.containsKey(prefix + block)) {
						prefix += "$";
					}
					blocks.put(prefix + block, null);
				} else {
					blocks.put(block, null);
				}
			} else { // two elements
				if (!cbm.getStateManager().canHaveData(Material.matchMaterial(block.split(":")[0]))) {
					cmr.error("Reward section " + this.getName() + " has a growth identifier on a non-growable block!");
					continue;
				}
				String blockStripped = block.split(":")[0];
				boolean data;
				if (block.split(":")[1].equalsIgnoreCase("true")) {
					data = true;
				} else if (block.split(":")[1].equalsIgnoreCase("false")) {
					data = false;
				} else {
					cmr.error("Reward section " + this.getName() + " has an invalid growth identifier under the block " + block.split(":")[0] + ".");
					continue;
				}
				if (blocks.containsKey(blockStripped)) {
					cmr.warning("Duplicate item (maybe different growth values) in blocks list of section " + this.getName() + ":  " + block + " with data value " + block.split(":")[1] + ".");
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
		set("blocks", newBlocks);
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
			if (!cbm.getStateManager().canHaveData(strippedBlock)) {
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
		if (gcm.containsMatch(blocks, block + "(?::.+)?")) {
			throw new BlockAlreadyInListException("The block " + block + " is already handled by the reward section " + this.getName() + "!");
		}
		blocks.add(block);
		cbm.addHandler(this, Material.matchMaterial(block));
		this.setBlocks(blocks);
	}
	public void addBlock(Material block) throws BlockAlreadyInListException {
		try {
			addBlock(block.toString());
		} catch (InvalidMaterialException e) {}
	}
	public void addBlock(Material block, String data) throws BlockAlreadyInListException {
		try {
			addBlock(block.toString(), data);
		} catch (InvalidMaterialException e) {} // this shouldn't ever happen
	}
	public void addBlock(String block, String data) throws BlockAlreadyInListException, InvalidMaterialException {
		validateBlock(block + ":" + data); // in case it came from addBlock(Material, String) or a direct call. Plus it's just a command, doesn't hurt to validate twice.
		List<String> blocks = this.getRawBlocks();
		if (gcm.containsMatch(blocks, block + "(?::.+)?")) { // "(?:" means just a group, don't save result
			throw new BlockAlreadyInListException("The block " + block + " is already handled by the reward section " + this.getName() + "!");
		}
		blocks.add(block + ":" + data);
		cbm.addCropHandler(this, Material.matchMaterial(block), Boolean.parseBoolean(data));
		this.setBlocks(blocks);
	}
	public void removeBlockRaw(String block, boolean hasData) throws BlockNotInListException {
		List<String> blocks = this.getRawBlocks();
		if (gcm.removeIgnoreCase(blocks, block)) {
			blocks.remove(block);
			if (hasData) {
				cbm.removeCropHandler(this, Material.matchMaterial(block.split(":")[0]), Boolean.parseBoolean(block.split(":")[1]));
			} else {
				cbm.removeHandler(this, Material.matchMaterial(block));
			}
			this.setBlocks(blocks);
		} else {
			throw new BlockNotInListException("The block " + block + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public void removeBlock(String block, Boolean data) throws BlockNotInListException {
		if (data == null) {
			removeBlockRaw(block, false);
		} else {
			removeBlockRaw(block + ":" + data, true);
		}
	}
	public void removeBlock(Material block) throws BlockNotInListException {
		removeBlockRaw(block.toString(), false);
	}
	public List<String> getAllowedWorlds() {
		return this.section.getStringList("allowedWorlds");
	}
	public void setAllowedWorlds(List<String> newAllowedWorlds) {
		set("allowedWorlds", newAllowedWorlds);
	}
	public void addAllowedWorld(String world) throws AreaAlreadyInListException, InvalidAreaException {
		if (gcm.containsIgnoreCase(this.getAllowedWorlds(), world)) {
			throw new AreaAlreadyInListException("The world " + world + " is already handled by the reward section " + this.getName() + "!");
		}
		if (!gcm.isValidWorld(world)) {
			throw new InvalidAreaException("The world " + world + " does not exist!");
		}
		List<String> worlds = this.getAllowedWorlds();
		worlds.add(world);
		this.setAllowedWorlds(worlds);
	}
	public void removeAllowedWorld(String world) throws AreaNotInListException {
		List<String> worlds = this.getAllowedWorlds();
		if (gcm.removeIgnoreCase(worlds, world)) {
			this.setAllowedWorlds(worlds);
		} else {
			throw new AreaNotInListException("The world " + world + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public boolean isWorldAllowed(String world) {
		List<String> worlds;
		if (getAllowedWorlds().size() == 0) {
			if (gcm.getGlobalAllowedWorlds().size() == 0) return true;
			worlds = gcm.getGlobalAllowedWorlds();
		} else {
			worlds = getAllowedWorlds();
		}
		return (worlds.contains("*")) || gcm.containsIgnoreCase(worlds, world);
	}
	public List<String> getAllowedRegions() {
		return this.section.getStringList("allowedRegions");
	}
	public void setAllowedRegions(List<String> newAllowedRegions) {
		set("allowedRegions", newAllowedRegions);
	}
	public void addAllowedRegion(String region) throws AreaAlreadyInListException {
		if (gcm.containsIgnoreCase(this.getAllowedRegions(), region)) {
			throw new AreaAlreadyInListException("The region " + region + " is already handled by the reward section " + this.getName() + "!");
		}
		List<String> regions = this.getAllowedRegions();
		regions.add(region);
		this.setAllowedRegions(regions);
	}
	public void removeAllowedRegion(String region) throws AreaNotInListException {
		List<String> regions = this.getAllowedRegions();
		if (gcm.removeIgnoreCase(regions, region)) {
			this.setAllowedRegions(regions);
		} else {
			throw new AreaNotInListException("The region " + region + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public SilkTouchPolicy getSilkTouchPolicy() {
		SilkTouchPolicy stp = SilkTouchPolicy.getByName(this.section.getString("silkTouch"));
		return stp == null ? SilkTouchPolicy.INHERIT : stp;
	}
	public void setSilkTouchPolicy(SilkTouchPolicy newRequirement) {
		set("silkTouch", newRequirement.toString());
	}
	public int getRewardLimit() {
		return Math.max(this.section.getInt("rewardLimit", -1), -1);
	}
	public void setRewardLimit(int rewardLimit) {
		set("rewardLimit", Math.max(rewardLimit, -1));
	}
	public void delete() {
		for (Entry<String,Boolean> entry : getBlocksWithData().entrySet()) {
			cbm.removeCropHandler(this, Material.matchMaterial(entry.getKey().replace("$", "")), entry.getValue());
		}
		gcm.getRewardsConfig().set(this.getPath(), null);
		gcm.saveRewardsConfig();
		cbm.unloadSection(getName());
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
		return children;
	}
	public String getPrettyChildren() {
		if (!this.section.isConfigurationSection("rewards")) {
			return "";
		}
		return gcm.makePretty(getChildrenNames());
	}
	public Reward getChild(String child) {
		for (Reward reward : getChildren()) {
			if (reward.getName().equalsIgnoreCase(child)) {
				return reward;
			}
		}
		return null;
	}
	public Reward getNewChild(String child) {
		if (this.section.isConfigurationSection("rewards")) {
			for (String childName : getChildrenNames()) {
				return new Reward(this, childName, false);
			}
		}
		cmr.warning("Section " + this.getName() + " tried to find new child " + child + " but failed.");
		return null;
	}
	protected void debug(String msg) {
		cmr.debug(msg);
	}
	public boolean isApplicable(BlockState state, Player player) {
		// block type is checked by CMRBlockHandler
		if (!isWorldAllowed(player.getWorld().getName())) {
			debug("Player was denied access to rewards in reward section because the reward section is not allowed in this world.");
			return false;
		}
		WorldGuardManager wgm = cmr.getWGManager();
		if (wgm.usingWorldGuard() && !wgm.isAllowedInRegions(this, state.getBlock())) {
			debug("Player was denied access to rewards in reward section because the reward section is not allowed in this region.");
			return false;
		}
		return true;
	}
	public int execute(BlockState state, Player player, int globalRewardLimit) {
		debug("Processing section " + this.getName());
		if (!isApplicable(state, player)) return 0;
		List<Reward> rewards = getChildren();
		int rewardLimit = -1; // this only carries through when both are -1
		// if both are equal to -1 this is false
		boolean isRewardLimit = globalRewardLimit != -1 || getRewardLimit() != -1;
		if (isRewardLimit) {
			if (globalRewardLimit == -1 || getRewardLimit() == -1) { // if one but not both are -1
				rewardLimit = Math.max(globalRewardLimit, getRewardLimit()); // neither can be less than -1 so this is ok
			} else { // both have a value
				rewardLimit = Math.min(globalRewardLimit, getRewardLimit());
			}
		}
		if (gcm.isRandomizingRewardOrder()) {
			Collections.shuffle(rewards);
		}
		int rewardsExecuted = 0;
		for (Reward reward : rewards) {
			if (reward.isApplicable(player)) {
				boolean rewardIssued = reward.execute(player);
				if (rewardIssued && isRewardLimit && ++rewardsExecuted >= rewardLimit) {
					debug("Reward limit reached, quitting");
					break;
				}
			}
		}
		return rewardsExecuted;
	}
	protected void set(String path, Object value) {
		set(path, value, false);
	}
	protected void set(String path, Object value, boolean noReload) {
		this.section.set(path, value);
		gcm.saveRewardsConfig();
		if (!noReload) {
			cbm.reloadSection(this.getName());
		}
	}
	public void unloadChild(String name) {
		cmr.debug("Section " + this.getName() + " is attempting to reload child " + name);
		Reward reload = null;
		for (Reward reward : children) {
			if (reward.getName().equals(name)) {
				reload = reward;
			}
		}
		if (reload == null) {
			cmr.warning("Section " + this.getName() + " attempted to reload child " + name + " but couldn't find it.");
			return;
		}
		children.remove(reload);
	}
	public void loadChild(String name) {
		children.add(new Reward(this, name, false));
	}
	public void reloadChild(String name) {
		unloadChild(name);
		loadChild(name);
	}
}
