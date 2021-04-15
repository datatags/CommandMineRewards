package me.datatags.commandminerewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.datatags.commandminerewards.commands.CMRTabComplete;
import me.datatags.commandminerewards.commands.CommandDispatcher;
import me.datatags.commandminerewards.gui.GUIListener;
import me.datatags.commandminerewards.hook.McMMOHook;
import me.datatags.commandminerewards.worldguard.WorldGuardManager;

public class CommandMineRewards extends JavaPlugin {
	private int minecraftVersion = -1;
	// asdf: A Simple Date Format not a random keyboard mash
	private GlobalConfigManager gcm;
	private WorldGuardManager wgm;
	private CMRBlockManager cbm;
	private McMMOHook mh = null;
	private static CommandMineRewards instance;
	private boolean pluginReady = false;
	@Override
	public void onEnable() {
		instance = this;
		this.gcm = GlobalConfigManager.getInstance();
		initVersion(); // this needs to be called before anything uses getMinecraftVersion()
		this.wgm = new WorldGuardManager(); // this can probably actually run about anytime
		gcm.load(); // this needs to run before RewardSections start loading
		this.cbm = CMRBlockManager.getInstance(); // not a priority, just to get the sort timer ticking
		if (gcm.isMcMMOHookEnabled()) {
			if (getServer().getPluginManager().getPlugin("mcMMO") != null) {
				mh = new McMMOHook();
			} else {
				CMRLogger.warning("mcMMO hooking is enabled in config.yml, but mcMMO was not found.");
			}
		}
		getServer().getPluginManager().registerEvents(new EventListener(mh), this); // initialize the block break listener
		getServer().getPluginManager().registerEvents(new GUIListener(), this);
		getCommand("cmr").setExecutor(CommandDispatcher.getInstance()); // initialize the commands
		getCommand("cmr").setTabCompleter(new CMRTabComplete(this)); // this can run anytime really
		new Metrics(this, 9691);
		CMRLogger.info("CommandMineRewards is enabled!");
		pluginReady = true;
	}
	public static CommandMineRewards getInstance() {
		return instance;
		// JavaPlugin is a singleton, and dependency injection isn't really practical
		// for some classes. Dependency injection is still used when possible.
	}
	public void reload() {
		gcm.load();
		cbm.reloadCache();
	}
	private void initVersion() {
		String ver = getFullMinecraftVersion();
		minecraftVersion = Integer.parseInt(ver.substring(2, ver.lastIndexOf('.'))); // group 0 is whole thing, group 1 is first group
		CMRLogger.debug("Minecraft version: 1." + minecraftVersion);
	}
	public int getMinecraftVersion() {
		return minecraftVersion;
	}
	public String getFullMinecraftVersion() {
		String ver = Bukkit.getBukkitVersion(); // ex: 1.15.2-R0.1-SNAPSHOT
		return ver.substring(0, ver.indexOf('-'));
	}
	public boolean isLegacyMinecraft() {
		return minecraftVersion < 13;
	}
	@Override
	public void onDisable() {
		CMRLogger.closeDebugLog();
		getLogger().info("CommandMineRewards has been disabled!");
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
