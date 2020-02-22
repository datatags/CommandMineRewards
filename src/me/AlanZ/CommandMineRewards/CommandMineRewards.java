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
import org.bukkit.plugin.java.JavaPlugin;

import me.AlanZ.CommandMineRewards.commands.CMRTabComplete;
import me.AlanZ.CommandMineRewards.commands.CommandDispatcher;
import me.AlanZ.CommandMineRewards.worldguard.WorldGuardManager;

public class CommandMineRewards extends JavaPlugin {
	private int minecraftVersion = -1;
	private File debugLog = null;
	private BufferedWriter debugWriter = null;
	// asdf: A Simple Date Format not a random keyboard mash
	private SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
	
	@Override
	public void onEnable() {
		GlobalConfigManager.cmr = this; // this needs to run before any other GCM calls or it won't have config access
		initDebugLog(); // this should run before any debug() calls
		initVersion(); // this needs to be called before anything uses getMinecraftVersion()
		WorldGuardManager.init(this); // this needs to run early so other plugins can use the RC API
		new CommandDispatcher(this);
		new CMRTabComplete(this); // this can run anytime really
		GlobalConfigManager.load(); // this needs to run before RewardSections start loading
		RewardSection.cmr = this; // needs to run before RS calls
		Reward.cmr = this; // probably also needs to run before RS calls for various reasons
		RewardSection.fillCache(); // is sorta optional but should run before other RS block access
		CMRBlockManager.initializeHandlers(this); // should run after cache is loaded but really just anytime before someone joins the server
		new EventListener(this);
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
	}
	public void reload() {
		initDebugLog();
	}
	private void initDebugLog() {
		if (debugWriter != null) {
			try {
				debugWriter.close();
			} catch (IOException e) {
				getLogger().warning("Failed to close debug log");
				e.printStackTrace();
			}
			debugWriter = null;
		}
		if (GlobalConfigManager.isDebugLog()) {
			try {
				File log = new File(this.getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					this.getLogger().info("CMR debug log was successfully created!");
				}
				debugLog = log;
				debugWriter = new BufferedWriter(new FileWriter(debugLog, true));
				debug("CMR has started up at " + asdf.format(new Date()), false);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				getLogger().warning("Failed to create CMR debug log. Do we have write permissions?");
				//e.printStackTrace();
			}
		}
	}
	private void initVersion() {
		String ver = getFullMinecraftVersion();
		minecraftVersion = Integer.parseInt(ver.substring(2, ver.lastIndexOf('.'))); // group 0 is whole thing, group 1 is first group
		debug("1." + minecraftVersion);
	}
	public int getMinecraftVersion() {
		return minecraftVersion;
	}
	public String getFullMinecraftVersion() {
		String ver = Bukkit.getBukkitVersion();
		return ver.substring(0, ver.indexOf('-'));
	}
	@Override
	public void onDisable() {
		debug("CMR has been shut down at " + asdf.format(new Date()) + "\n\n\n\n", false);
		try {
			debugWriter.close();
		} catch (IOException e) {
			getLogger().warning("Failed to close debug log handle:");
			e.printStackTrace();
		}
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
					debugWriter.append(msg + "\n");
					debugWriter.flush();
				} catch (IOException e) {
					getLogger().severe("Failed to write to CMR debug log! Do we have write permission?");
					e.printStackTrace();
				}
			}
		}
	}
	@SuppressWarnings("deprecation") // because < 1.9 doesn't have main hand/offhand
	public ItemStack getItemInHand(Player player) {
		if (getMinecraftVersion() < 9) {
			return player.getItemInHand();
		} else {
			return player.getInventory().getItemInMainHand();
		}
	}
}
