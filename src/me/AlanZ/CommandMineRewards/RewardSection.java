package me.AlanZ.CommandMineRewards;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import me.AlanZ.CommandMineRewards.Exceptions.BlockAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.BlockNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidMaterialException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionNotInListException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldNotInListException;

public class RewardSection {
	private ConfigurationSection section;
	static CommandMineRewards cmr = null;
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
	public List<String> getRawBlocks() {
		return this.section.getStringList("blocks");
	}
	public List<String> getBlocks() {
		List<String> rv = new ArrayList<String>();
		for (String block : getRawBlocks()) {
			rv.add(block.split(":")[0].toLowerCase());
		}
		return rv;
	}
	public List<Entry<String,Byte>> getBlocksWithData() { // yeah, couldn't really think of a better way of doing it other than returning an Entry<List<String>,List<Byte>>
		List<Entry<String,Byte>> list = new ArrayList<Entry<String,Byte>>();
		for (String block : getRawBlocks()) {
			try {
				if (block.split(":").length == 1) {
					list.add(new AbstractMap.SimpleEntry<String,Byte>(block, (byte) 0));
				} else {
					if (block.split(":")[1].equals("*")) {
						list.add(new AbstractMap.SimpleEntry<String,Byte>(block.split(":")[0], Byte.MAX_VALUE));
					} else {
						list.add(new AbstractMap.SimpleEntry<String,Byte>(block.split(":")[0], Byte.parseByte(block.split(":")[1])));
					}
					
				}
			} catch (NumberFormatException e) {
				cmr.getLogger().severe("Reward section " + this.getName() + " has an invalid data value under the block " + block.split(":")[0] + ".");
				continue;
			}
		}
		return list;
	}
	public void setBlocks(List<String> newBlocks) {
		this.section.set("blocks", newBlocks);
		cmr.saveConfig();
	}
	private void validateBlock(String block) throws InvalidMaterialException {
		if (Material.matchMaterial(block) == null) {
			throw new InvalidMaterialException(block + " is not a valid block, it's not even a valid item.");
		}
		if (!Material.matchMaterial(block).isBlock()) {
			throw new InvalidMaterialException(block + " is not a block!");
		}
		if (Material.matchMaterial(block) == Material.AIR) {
			throw new InvalidMaterialException("You can't add air as a reward-triggering block!");
		}
	}
	public void addBlock(String block) throws BlockAlreadyInListException, InvalidMaterialException {
		byte data = 0;
		if (block.contains(":")) {
			if (block.split(":")[1].equals("*")) {
				data = Byte.MAX_VALUE;
			} else {
				try {
					data = Byte.parseByte(block.split(":")[1]);
				} catch (NumberFormatException e) {
					throw new InvalidMaterialException("Could not parse data value " + block.split(":")[1]);
				}
			}
			block = block.split(":")[0];
		}
		addBlock(block, data);
	}
	public void addBlock(Material block) throws BlockAlreadyInListException, InvalidMaterialException {
		addBlock(block.toString(), (byte)0);
	}
	public void addBlock(Material block, byte data) throws BlockAlreadyInListException, InvalidMaterialException {
		addBlock(block.toString(), data);
	}
	public void addBlock(String block, byte data) throws BlockAlreadyInListException, InvalidMaterialException {
		validateBlock(block);
		if (GlobalConfigManager.containsIgnoreCase(this.getRawBlocks(), block + ":" + data)) {
			throw new BlockAlreadyInListException("The block " + block + ":" + data + " is already handled by the reward section " + this.getName() + "!");
		}
		List<String> blocks = this.getRawBlocks();
		cmr.debug(block);
		if (data == (byte) 0) {
			blocks.add(block);
		} else {
			if (data == Byte.MAX_VALUE) {
				blocks.add(block + ":*");
			} else {
				blocks.add(block + ":" + data);
			}
			
		}
		this.setBlocks(blocks);
	}
	public void removeBlock(String block) throws BlockNotInListException {
		List<String> blocks = this.getRawBlocks();
		if (GlobalConfigManager.removeIgnoreCase(blocks, block)) {
			blocks.remove(block);
			this.setBlocks(blocks);
		} else {
			throw new BlockNotInListException("The block " + block + " is not handled by the reward section " + this.getName() + "!");
		}
	}
	public void removeBlock(String block, byte data) throws BlockNotInListException {
		removeBlock(block + ":" + data);
	}
	public void removeBlock(Material block) throws BlockNotInListException {
		removeBlock(block.toString());
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
		if (GlobalConfigManager.isValidWorld(world)) {
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
		cmr.getConfig().set(this.getPath(), null);
		cmr.saveConfig();
	}
	public String getName() {
		return this.section.getName();
	}
	public String getPath() {
		return this.section.getCurrentPath();
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
}
