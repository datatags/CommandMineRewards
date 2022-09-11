package me.datatags.commandminerewards;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;

import me.datatags.commandminerewards.commands.CMRTabComplete;
import me.datatags.commandminerewards.commands.CommandDispatcher;
import me.datatags.commandminerewards.gui.GUIListener;
import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.hook.McMMOManager;
import me.datatags.commandminerewards.hook.WorldGuardManager;

public class CommandMineRewards extends JavaPlugin {
    private int minecraftVersion = -1;
    private GlobalConfigManager gcm;
    private WorldGuardManager wgm;
    private CMRBlockManager cbm;
    private McMMOManager mcmmo;
    private static CommandMineRewards instance;
    private boolean pluginReady = false;

    @Override
    public void onEnable() {
        instance = this;
        this.gcm = GlobalConfigManager.getInstance();
        initVersion(); // this needs to be called before anything uses getMinecraftVersion()
        this.wgm = new WorldGuardManager(); // this can probably actually run about anytime
        this.mcmmo = new McMMOManager();
        this.cbm = CMRBlockManager.getInstance();
        getServer().getPluginManager().registerEvents(new EventListener(mcmmo), this); // initialize the block break listener
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getCommand("cmr").setExecutor(CommandDispatcher.getInstance()); // initialize the commands
        getCommand("cmr").setTabCompleter(new CMRTabComplete(this)); // this can run anytime really
        Metrics metrics = new Metrics(this, 9691);
        metrics.addCustomChart(new SimplePie("mcmmo", new Callable<String>() {
            @Override
            public String call() {
                if (!mcmmo.isMcMMOPresent()) {
                    return "not present";
                } else if (!mcmmo.isHookEnabled()) {
                    return "present, hook disabled";
                } else {
                    return "hook enabled";
                }
            }
        }));
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
        String ver = getFullMinecraftVersion().substring(2);
        if (ver.contains(".")) {
            ver = ver.substring(0, ver.indexOf('.'));
        }
        minecraftVersion = Integer.parseInt(ver);
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
        GUIManager.getInstance().closeAll();
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
