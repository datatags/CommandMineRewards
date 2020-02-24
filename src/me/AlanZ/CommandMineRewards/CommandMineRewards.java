package me.AlanZ.CommandMineRewards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

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
		WorldGuardManager.init(this); // this can probably actually run about anytime
		new CommandDispatcher(this);
		new CMRTabComplete(this); // this can run anytime really
		GlobalConfigManager.load(); // this needs to run before RewardSections start loading
		RewardSection.cmr = this; // needs to run before RS calls
		Reward.cmr = this; // probably also needs to run before RS calls for various reasons
		RewardSection.init(); // is sorta optional but should run before other RS block access
		CMRBlockManager.initializeHandlers(this); // should run after cache is loaded but really just anytime before someone joins the server
		new EventListener(this);
		info("CommandMineRewards is enabled!");
	}
	public void reload() {
		initDebugLog();
	}
	private void initDebugLog() { // DON'T use this.logMessage() in this method, it won't work
		if (debugWriter != null) {
			try {
				debugWriter.close();
			} catch (IOException e) {
				getLogger().warning("Failed to close debug log");
				e.printStackTrace();
			}
		}
		if (GlobalConfigManager.isDebugLog()) {
			try {
				File log = new File(this.getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				int mega = 1024 * 1024;
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					this.getLogger().info("CMR debug log was successfully created!");
				} else if (log.length() > 8 * mega) {
					getLogger().severe("Your debug.log is over 8MB, we won't log any more debug messages until you delete it.");
					return;
				} else if (log.length() > mega) {
					getLogger().warning("Your debug.log is over 1MB, you should probably reset it or disable it if you don't need it.");
				}
				debugLog = log;
				debugWriter = new BufferedWriter(new FileWriter(debugLog, true));
				debug("CMR has started up at " + asdf.format(new Date()), false);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				getLogger().severe("Failed to create CMR debug log. Do we have write permissions?");
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
		String ver = Bukkit.getBukkitVersion(); // ex: 1.15.2-R0.1-SNAPSHOT
		return ver.substring(0, ver.indexOf('-'));
	}
	@Override
	public void onDisable() {
		debug("CMR has been shut down at " + asdf.format(new Date()) + "\n\n\n\n", false);
		if (debugWriter != null) {
			try {
				debugWriter.close(); // don't use this.logMessage() past here, it won't work
			} catch (IOException e) {
				getLogger().warning("Failed to close debug log handle:");
				e.printStackTrace();
			}
		}
		getLogger().info("CommandMineRewards has been disabled!");
	}
	public void logMessage(String msg) {
		logMessage(msg, Level.FINE);
	}
	public void logMessage(String msg, Level level) {
		logMessage(msg, level, true);
	}
	public void logMessage(String msg, Level level, boolean logToConsole) {
		if (logToConsole) getLogger().log(level.intValue() < Level.INFO.intValue() ? Level.INFO : level, msg); // nothing lower than info will be logged
		if (GlobalConfigManager.isDebugLog()) {
			try {
				debugWriter.append("[" + level.toString().replace("FINE", "DEBUG") + "] " + msg + "\n");
				debugWriter.flush();
			} catch (IOException e) {
				getLogger().severe("Failed to write to CMR debug log! Do we have write permission?");
				e.printStackTrace();
			}
		}
	}
	public void debug(String msg) {
		debug(msg, true);
	}
	public void debug(String msg, boolean logToConsole) {
		logMessage(msg, Level.FINE, GlobalConfigManager.getDebug() && logToConsole); // we use log level FINE then substitute it for debug or INFO where required
	}
	public void info(String msg) {
		logMessage(msg, Level.INFO);
	}
	public void warning(String msg) {
		logMessage(msg, Level.WARNING);
	}
	public void error(String msg) {
		logMessage(msg, Level.SEVERE);
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
