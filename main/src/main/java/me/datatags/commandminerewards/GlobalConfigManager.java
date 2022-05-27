package me.datatags.commandminerewards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;

public class GlobalConfigManager {
    private static GlobalConfigManager instance = null;
    private File rewardsFile;
    private YamlConfiguration rewardsConfig;
    private CommandMineRewards cmr = CommandMineRewards.getInstance();
    private GlobalConfigManager() {
        load();
    }
    public static GlobalConfigManager getInstance() {
        if (instance == null) {
            instance = new GlobalConfigManager();
        }
        return instance;
    }
    private ConfigurationSection getConfig() {
        return cmr.getConfig();
    }
    public void load() {
        loadRewardsConfig();
        cmr.saveDefaultConfig();
        cmr.reloadConfig();
        checkOldConfig();
    }
    private void loadRewardsConfig() {
        rewardsFile = new File(cmr.getDataFolder() + File.separator + "rewards.yml");
        if (!rewardsFile.exists()) {
            cmr.getDataFolder().mkdirs();
            cmr.saveResource("rewards.yml", false);
        }
        rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
    }
    public void saveRewardsConfig() {
        try {
            rewardsConfig.save(rewardsFile);
        } catch (IOException e) {
            CMRLogger.warning("Failed to save rewards.yml:");
            e.printStackTrace();
        }
    }
    public YamlConfiguration getRewardsConfig() {
        return rewardsConfig;
    }
    private void checkOldConfig() {
        // we don't clear the debug log setting because if the plugin is spamming the log,
        // the plugin is in verbosity: 2 which no one in their right mind would use except for troubleshooting
        boolean changed = false;
        if (getConfig().contains("debug")) {
            getConfig().set("verbosity", getConfig().getBoolean("debug") ? 2 : 1);
            getConfig().set("debug", null);
            changed = true;
        }
        for (String key : getConfig().getKeys(false)) {
            if (getConfig().isConfigurationSection(key)) {
                moveToRewards(key);
                changed = true;
            }
        }
        // bitwise or, no short circuiting.
        if (moveToRewards("multiplier") | moveToRewards("globalRewardLimit")) {
            changed = true;
        }
        if (changed) {
            cmr.saveConfig();
            saveRewardsConfig();
            CMRLogger.info("Successfully migrated config!");
        }
    }
    private boolean moveToRewards(String key) {
        if (getConfig().contains(key)) {
            rewardsConfig.set(key, getConfig().get(key));
            getConfig().set(key, null);
            return true;
        }
        return false;
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
        return getConfig().getStringList("globalAllowedWorlds");
    }
    public void setGlobalAllowedWorlds(List<String> newAllowedWorlds) {
        getConfig().set("globalAllowedWorlds", newAllowedWorlds);
        cmr.saveConfig();
    }
    public void addGlobalAllowedWorld(String world) throws AreaAlreadyInListException, InvalidAreaException {
        if (containsIgnoreCase(getGlobalAllowedWorlds(), world)) {
            throw new AreaAlreadyInListException("The world " + world + " is already globally allowed.");
        }
        if (!isValidWorld(world)) {
            throw new InvalidAreaException("There is no world with the name " + world);
        }
        List<String> worlds = getGlobalAllowedWorlds();
        worlds.add(world);
        setGlobalAllowedWorlds(worlds);
    }
    public void removeGlobalAllowedWorld(String world) throws AreaNotInListException {
        if (!containsIgnoreCase(getGlobalAllowedWorlds(), world)) {
            throw new AreaNotInListException("The world " + world + " is not globally allowed.");
        }
        List<String> worlds = getGlobalAllowedWorlds();
        worlds.remove(world);
        setGlobalAllowedWorlds(worlds);
    }
    public List<String> getGlobalAllowedRegions() {
        return getConfig().getStringList("globalAllowedRegions");
    }
    public void setGlobalAllowedRegions(List<String> newAllowedRegions) {
        getConfig().set("globalAllowedRegions", newAllowedRegions);
        cmr.saveConfig();
    }
    public void addGlobalAllowedRegion(String region) throws AreaAlreadyInListException, InvalidAreaException {
        if (containsIgnoreCase(getGlobalAllowedRegions(), region)) {
            throw new AreaAlreadyInListException("The WorldGuard region " + region + " is already globally allowed.");
        }
        if (!cmr.getWGManager().isValidRegion(region)) {
            throw new InvalidAreaException("There is no such WorldGuard region with the name " + region + ".");
        }
        List<String> regions = getGlobalAllowedRegions();
        regions.add(region);
        setGlobalAllowedRegions(regions);
    }
    public void removeGlobalAllowedRegion(String region) throws AreaNotInListException {
        if (!containsIgnoreCase(getGlobalAllowedRegions(), region)) {
            throw new AreaNotInListException("The WorldGuard region " + region + " is not globally allowed!");
        }
        List<String> regions = getGlobalAllowedRegions();
        regions.remove(region);
        setGlobalAllowedRegions(regions);
    }
    public SilkTouchPolicy getGlobalSilkTouchPolicy() {
        SilkTouchPolicy stp = SilkTouchPolicy.getByName(getConfig().getString("globalSilkTouch"));
        return stp == null ? SilkTouchPolicy.INHERIT : stp;
    }
    public void setGlobalSilkTouchPolicy(SilkTouchPolicy newRequirement) {
        getConfig().set("globalSilkTouch", newRequirement.toString());
        cmr.saveConfig();
    }
    public SilkTouchPolicy getSilkTouchPolicy(RewardGroup rewardGroup, Reward reward) {
        if (reward != null && reward.getSilkTouchPolicy() != SilkTouchPolicy.INHERIT) {
            return reward.getSilkTouchPolicy();
        } else if (rewardGroup != null && rewardGroup.getSilkTouchPolicy() != SilkTouchPolicy.INHERIT) {
            return rewardGroup.getSilkTouchPolicy();
        } else if (getGlobalSilkTouchPolicy() != SilkTouchPolicy.INHERIT) {
            return getGlobalSilkTouchPolicy();
        } else {
            return SilkTouchPolicy.IGNORED;
        }
    }
    public boolean silkStatusAllowed(RewardGroup rewardGroup, Reward reward, boolean silkTouch) {
        SilkTouchPolicy current = getSilkTouchPolicy(rewardGroup, reward);
        if (current == SilkTouchPolicy.IGNORED) {
            return true;
        } else if (current == SilkTouchPolicy.REQUIRED && silkTouch) {
            return true;
        } else if (current == SilkTouchPolicy.DISALLOWED && !silkTouch) {
            return true;
        } else {
            return false;
        }
    }
    public double getMultiplier() {
        return getRewardsConfig().getDouble("multiplier");
    }
    public void setMultiplier(double newMultiplier) {
        getRewardsConfig().set("multiplier", newMultiplier);
        saveRewardsConfig();
    }
    public boolean isDebug() {
        return getConfig().getInt("verbosity", 1) > 1;
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
    public boolean isAutopickupCompat() {
        return getConfig().getBoolean("autopickupCompat", false);
    }
    public int getGlobalRewardLimit() {
        return Math.max(getRewardsConfig().getInt("globalRewardLimit", -1), -1); // return -1 if not found and return -1 if found is less than -1
    }
    public void setGlobalRewardLimit(int newLimit) {
        getRewardsConfig().set("globalRewardLimit", Math.max(newLimit, -1));
    }
    public boolean isRandomizingRewardOrder() {
        return getConfig().getBoolean("randomizeRewardOrder", false);
    }
    public boolean isMcMMOHookEnabled() {
        return getConfig().getBoolean("mcMMOHookEnabled", false);
    }
    public List<String> getRewardSectionNames() {
        List<String> names = new ArrayList<>();
        for (String key : getRewardsConfig().getKeys(false)) {
            if (!getRewardsConfig().isConfigurationSection(key)) { 
                continue;
            }
            names.add(key);
        }
        return names;
    }
    public List<RewardGroup> getRewardGroups() {
        List<RewardGroup> rv = new ArrayList<>();
        for (String name : getRewardSectionNames()) {
            rv.add(new RewardGroup(name));
        }
        return rv;
    }
    public String getPrettyRewardSections() {
        return makePretty(getRewardSectionNames());
    }
    public boolean isValidWorld(String test) {
        if (!isValidatingWorldsAndRegions()) {
            return true;
        }
        if (test.equals("*")) return true;
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
