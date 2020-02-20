package me.AlanZ.CommandMineRewards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand;
import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand_1_8;
import me.AlanZ.CommandMineRewards.ItemInHand.ItemInHand_1_9;
import me.AlanZ.CommandMineRewards.commands.CMRTabComplete;
import me.AlanZ.CommandMineRewards.commands.CommandDispatcher;

public class CommandMineRewards extends JavaPlugin {
	
	private static File debugLog = null;
	private ItemInHand iih = null;
	private boolean worldGuardLoaded = false;
	// asdf: A Simple Date Format not a random keyboard mash
	private SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
	
	@Override
	public void onEnable() {
		GlobalConfigManager.cmr = this; // this needs to run before any other GCM calls or it won't have config access
		initDebugLog(); // this should run before any debug() calls
		new CommandDispatcher(this);
		new CMRTabComplete(this); // this can run anytime really
		GlobalConfigManager.load(); // this needs to run before RewardSections start loading
		RewardSection.cmr = this; // needs to run before RS calls
		Reward.cmr = this; // probably also needs to run before RS calls for various reasons
		RewardSection.fillCache(); // is sorta optional but should run before other RS block access
		CMRBlockManager.initializeHandlers(this); // should run after cache is loaded but really just anytime before someone joins the server
		if (worldGuardLoaded = isWorldGuardLoaded()) {
			getLogger().info("Found WorldGuard.  Using to check regions.");
		} else {
			getLogger().info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
		}
		new EventListener(this);
		if (!initItemInHand()) {
			getLogger().severe("Could not determine server version, plugin will not work properly!");
		}
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
	}
	private void initDebugLog() {
		if (GlobalConfigManager.isDebugLog() && debugLog == null) {
			try {
				File log = new File(this.getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					this.getLogger().info("CMR debug log was successfully created!");
				}
				debugLog = log;
				debug("CMR has started up at " + asdf.format(new Date()));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				getLogger().warning("Failed to create CMR debug log. Do we have write permissions?");
				//e.printStackTrace();
			}
		}
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
		debug("CMR has been shut down at " + asdf.format(new Date()) + "\n\n\n\n", false);
		getLogger().info("CommandMineRewards (by AlanZ) has been disabled!");
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
	public ItemStack getItemInHand(Player player) {
		return iih.getItemInHand(player);
	}
}
