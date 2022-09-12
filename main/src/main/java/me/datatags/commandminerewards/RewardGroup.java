package me.datatags.commandminerewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.BlockAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.BlockNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;
import me.datatags.commandminerewards.Exceptions.InvalidMaterialException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.Exceptions.RewardGroupAlreadyExistsException;
import me.datatags.commandminerewards.hook.WorldGuardManager;

public class RewardGroup {
    private ConfigurationSection group;
    private CommandMineRewards cmr;
    private GlobalConfigManager gcm;
    private CMRBlockManager cbm;
    private List<CMRBlockState> blocksCache = null;
    private List<Reward> children = null;

    public static String isValidChars(String name) {
        String invalidChar = null;
        if (name.contains(".")) {
            invalidChar = "period";
        }
        if (name.contains(" ")) {
            invalidChar = "space";
        }
        return invalidChar;
    }

    public RewardGroup(String path, boolean create) {
        init();
        String badChar = isValidChars(path);
        if (badChar != null) {
            throw new InvalidRewardGroupException("You cannot use " + badChar + "s in reward group names!");
        }
        if (!gcm.getRewardsConfig().isConfigurationSection(path) && !create) { // if we couldn't find it easily and we're not supposed to create it,
            if (gcm.searchIgnoreCase(path, "") == null) { // search for it
                throw new InvalidRewardGroupException("Reward group " + path + " does not exist!"); // if we still couldn't find it, throw an exception
            }
            path = gcm.searchIgnoreCase(path, "");
        } else if (gcm.getRewardsConfig().isConfigurationSection(path) && create) {
            throw new RewardGroupAlreadyExistsException("Reward group " + path + " already exists!");
        }
        if (!gcm.getRewardsConfig().isConfigurationSection(path) && create) {
            group = gcm.getRewardsConfig().createSection(path);
            gcm.saveRewardsConfig();
            cbm.loadGroup(getName());
        } else {
            group = gcm.getRewardsConfig().getConfigurationSection(path);
        }
        postinit();
    }

    public RewardGroup(String path) {
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
        if (this.group.isConfigurationSection("rewards")) {
            List<Reward> rv = new ArrayList<>();
            for (String child : getChildrenNames()) {
                rv.add(new Reward(this, child, false));
            }
            children = rv;
        } else {
            children = new ArrayList<>();
        }
        registerPermissions();
    }

    private void registerPermissions() {
        for (Reward reward : this.getChildren()) {
            String permission = "cmr.use." + group.getName() + "." + reward.getName();
            if (Bukkit.getPluginManager().getPermission(permission) == null) {
                CMRLogger.debug("Adding permission " + permission);
                Bukkit.getPluginManager().addPermission(new Permission(permission));
            }
        }

    }

    public List<String> getRawBlocks() {
        if (blocksCache == null) { // if not cached
            CMRLogger.warning("Reward group " + this.getName() + "'s block list was not cached. Was it just created?");
            blocksCache = validateBlocks(); // validate it and cache the result.
        }
        List<String> raw = new ArrayList<>();
        blocksCache.forEach(c -> raw.add(c.compact()));
        return raw;
    }

    private List<CMRBlockState> validateBlocks() {
        boolean log = !cmr.isPluginReady(); // don't log it except on the initial run
        List<CMRBlockState> blocks = new ArrayList<>();
        for (String block : this.group.getStringList("blocks")) {
            CMRBlockState state;
            try {
                state = new CMRBlockState(block, cbm.getStateManager());
            } catch (IllegalArgumentException e) {
                if (log) {
                    CMRLogger.error(e.getMessage());
                }
                continue;
            }
            debug("Adding block " + state.compact() + " to group " + this.getName());
            blocks.add(state);
        }
        if (gcm.removeInvalidValues()) {
            List<String> newBlocks = new ArrayList<>();
            blocks.forEach(s -> newBlocks.add(s.compact()));
            set("blocks", blocks, true);
        }
        return blocks;
    }

    public List<String> getBlockTypes() {
        List<String> rv = new ArrayList<>();
        for (CMRBlockState state : getBlocks()) {
            rv.add(state.getType().toString().toLowerCase());
        }
        return rv;
    }

    public List<CMRBlockState> getBlocks() {
        return blocksCache;
    }

    public void setBlocks(List<String> newBlocks) {
        set("blocks", newBlocks);
    }

    public void saveBlocks() {
        List<String> rawBlocks = new ArrayList<>();
        blocksCache.forEach(c -> rawBlocks.add(c.compact()));
        setBlocks(rawBlocks);
    }

    public void addBlock(String block) throws BlockAlreadyInListException, InvalidMaterialException {
        CMRBlockState state;
        try {
            state = new CMRBlockState(block, cbm.getStateManager());
        } catch (IllegalArgumentException e) {
            throw new InvalidMaterialException(e.getMessage(), e);
        }
        for (CMRBlockState testState : getBlocks()) {
            if (state.getType() == testState.getType()) {
                throw new BlockAlreadyInListException("The block " + block + " is already handled by the reward group " + this.getName() + "!");
            }
        }
        blocksCache.add(state);
        cbm.addCropHandler(this, state);
        saveBlocks();
    }

    public void addBlock(String block, String data) throws BlockAlreadyInListException, InvalidMaterialException {
        addBlock(block + ":" + data);
    }

    public void addBlock(Material block) throws BlockAlreadyInListException {
        try {
            addBlock(block.toString());
        } catch (InvalidMaterialException e) {
        }
    }

    public void addBlock(Material block, String data) throws BlockAlreadyInListException {
        try {
            addBlock(block.toString(), data);
        } catch (InvalidMaterialException e) {
        } // this shouldn't ever happen
    }

    public void removeBlockState(CMRBlockState state) throws BlockNotInListException {
        boolean found = false;
        Iterator<CMRBlockState> iter = getBlocks().iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(state)) {
                iter.remove();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new BlockNotInListException("The block " + state.getType().toString() + " is not handled by the reward group " + this.getName() + "!");
        }
        cbm.removeCropHandler(this, state);
        saveBlocks();
    }

    public void removeBlockRaw(String block) throws BlockNotInListException {
        try {
            removeBlockState(new CMRBlockState(block, cbm.getStateManager()));
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    public void removeBlock(String block, Boolean data) throws BlockNotInListException {
        if (data == null) {
            removeBlockRaw(block);
        } else {
            removeBlockRaw(block + ":" + data);
        }
    }

    public void removeBlock(Material block) throws BlockNotInListException {
        removeBlockRaw(block.toString());
    }

    public List<String> getAllowedWorlds() {
        return this.group.getStringList("allowedWorlds");
    }

    public void setAllowedWorlds(List<String> newAllowedWorlds) {
        set("allowedWorlds", newAllowedWorlds);
    }

    public void addAllowedWorld(String world) throws AreaAlreadyInListException, InvalidAreaException {
        if (gcm.containsIgnoreCase(this.getAllowedWorlds(), world)) {
            throw new AreaAlreadyInListException("The world " + world + " is already handled by the reward group " + this.getName() + "!");
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
            throw new AreaNotInListException("The world " + world + " is not handled by the reward group " + this.getName() + "!");
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
        return this.group.getStringList("allowedRegions");
    }

    public void setAllowedRegions(List<String> newAllowedRegions) {
        set("allowedRegions", newAllowedRegions);
    }

    public void addAllowedRegion(String region) throws AreaAlreadyInListException {
        if (gcm.containsIgnoreCase(this.getAllowedRegions(), region)) {
            throw new AreaAlreadyInListException("The region " + region + " is already handled by the reward group " + this.getName() + "!");
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
            throw new AreaNotInListException("The region " + region + " is not handled by the reward group " + this.getName() + "!");
        }
    }

    public SilkTouchPolicy getSilkTouchPolicy() {
        SilkTouchPolicy stp = SilkTouchPolicy.getByName(this.group.getString("silkTouch"));
        return stp == null ? SilkTouchPolicy.INHERIT : stp;
    }

    public void setSilkTouchPolicy(SilkTouchPolicy newRequirement) {
        set("silkTouch", newRequirement.toString());
    }

    public int getRewardLimit() {
        return Math.max(this.group.getInt("rewardLimit", -1), -1);
    }

    public void setRewardLimit(int rewardLimit) {
        set("rewardLimit", Math.max(rewardLimit, -1));
    }

    public void delete() {
        cbm.removeHandlers(getName());
        gcm.getRewardsConfig().set(this.getPath(), null);
        gcm.saveRewardsConfig();
        cbm.unloadGroup(getName());
    }

    public String getName() {
        return this.group.getName();
    }

    public String getPath() {
        return this.group.getCurrentPath();
    }

    public List<String> getChildrenNames() {
        if (!this.group.isConfigurationSection("rewards")) {
            return new ArrayList<>();
        }
        List<String> rv = new ArrayList<>();
        for (String child : this.group.getConfigurationSection("rewards").getKeys(false)) {
            if (!this.group.isConfigurationSection("rewards." + child)) {
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
        if (!this.group.isConfigurationSection("rewards")) {
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
        if (this.group.isConfigurationSection("rewards")) {
            for (String childName : getChildrenNames()) {
                return new Reward(this, childName, false);
            }
        }
        CMRLogger.warning("group " + this.getName() + " tried to find new child " + child + " but failed.");
        return null;
    }

    protected void debug(String msg) {
        CMRLogger.debug(msg);
    }

    public boolean isApplicable(BlockState state, Player player) {
        // block type is checked by CMRBlockHandler
        if (getRewardLimit() == 0) {
            debug("Group " + this.getName() + " was skipped because it has a reward limit of 0");
            return false;
        }
        if (!isWorldAllowed(player.getWorld().getName())) {
            debug("Player was denied access to rewards in reward group because the reward group is not allowed in this world.");
            return false;
        }
        WorldGuardManager wgm = cmr.getWGManager();
        if (wgm.usingWorldGuard() && !wgm.isAllowedInRegions(this, state.getBlock())) {
            debug("Player was denied access to rewards in reward group because the reward group is not allowed in this region.");
            return false;
        }
        return true;
    }

    public int execute(BlockState state, Player player, double multiplier, int globalRewardLimit) {
        debug("Processing group " + this.getName());
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
                boolean rewardIssued = reward.execute(player, multiplier);
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
        this.group.set(path, value);
        gcm.saveRewardsConfig();
        if (!noReload) {
            cbm.reloadGroup(this.getName());
        }
    }

    public void unloadChild(String name) {
        debug("group " + this.getName() + " is attempting to unload child " + name);
        Reward reload = null;
        for (Reward reward : children) {
            if (reward.getName().equals(name)) {
                reload = reward;
            }
        }
        if (reload == null) {
            CMRLogger.warning("group " + this.getName() + " attempted to unload child " + name + " but couldn't find it.");
            return;
        }
        children.remove(reload);
    }

    public void loadChild(String name) {
        loadChild(new Reward(this, name, false));
    }

    public void loadChild(Reward reward) {
        children.add(reward);
    }

    public void reloadChild(String name) {
        unloadChild(name);
        loadChild(name);
    }
}
