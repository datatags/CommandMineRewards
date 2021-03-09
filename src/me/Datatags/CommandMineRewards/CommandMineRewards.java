package me.Datatags.CommandMineRewards;

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

import me.Datatags.CommandMineRewards.commands.CMRTabComplete;
import me.Datatags.CommandMineRewards.commands.CommandDispatcher;
import me.Datatags.CommandMineRewards.worldguard.WorldGuardManager;

public class CommandMineRewards extends JavaPlugin {
	private int minecraftVersion = -1;
	private File debugLog = null;
	private BufferedWriter debugWriter = null;
	// asdf: A Simple Date Format not a random keyboard mash
	private SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
	private GlobalConfigManager gcm;
	private WorldGuardManager wgm;
	private static CommandMineRewards instance;
	private boolean pluginReady = false;
	
	@Override
	public void onEnable() {
		instance = this;
		this.gcm = GlobalConfigManager.getInstance();
		initDebugLog(); // this should run before any debug() calls
		initVersion(); // this needs to be called before anything uses getMinecraftVersion()
		this.wgm = new WorldGuardManager(this); // this can probably actually run about anytime
		new CMRTabComplete(this); // this can run anytime really
		gcm.load(); // this needs to run before RewardSections start loading
		CMRBlockManager.getInstance(); // not a priority, just to get the sort timer ticking
		new EventListener(this); // initialize the block break listener
		CommandDispatcher.getInstance(); // initialize the commands
		info("CommandMineRewards is enabled!");
		pluginReady = true;
	}
	public static CommandMineRewards getInstance() {
		return instance;
		// JavaPlugin is a singleton, and dependency injection isn't really practical
		// for some classes. Dependency injection is still used when possible.
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
		if (gcm.isDebugLog()) {
			try {
				File log = new File(this.getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				int mega = 1024 * 1024;
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					this.getLogger().info("CMR debug log was successfully created!");
				} else if (log.length() > 8 * mega) {
					getLogger().severe("Your debug.log is over 8MB, we won't log any more debug messages until you reset it.");
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
	public boolean isLegacyMinecraft() {
		return minecraftVersion < 13;
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
		if (logToConsole) getLogger().log(level.intValue() < Level.INFO.intValue() ? Level.INFO : level, msg); // nothing lower than info will be logged so convert it to info
		if (gcm.isDebugLog()) {
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
		logMessage(msg, Level.FINE, gcm.getDebug() && logToConsole); // we use log level FINE then substitute it for debug or INFO where required
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
	public boolean isPluginReady() {
		return pluginReady;
	}
	public WorldGuardManager getWGManager() {
		return wgm;
	}
}
