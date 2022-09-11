package me.datatags.commandminerewards.hook;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.hook.interfaces.McMMOHook;
import me.datatags.commandminerewards.hook.legacy.LegacyMcMMOHook;

public class McMMOManager {
    private final boolean mcMMOPresent;
    private McMMOHook hook = null;

    public McMMOManager() {
        Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
        mcMMOPresent = mcMMO != null && mcMMO.isEnabled();
        if (!mcMMOPresent) {
            if (GlobalConfigManager.getInstance().isMcMMOHookEnabled()) {
                CMRLogger.warning("mcMMO hooking is enabled in config.yml, but mcMMO is missing or did not load correctly.");
            }
            return;
        }
        boolean legacy;
        try {
            legacy = mcMMO.class.getDeclaredMethod("getPlaceStore").getReturnType().getName().contains("chunkmeta");
        } catch (NoSuchMethodException | SecurityException e) {
            CMRLogger.error("Failed to determine mcMMO version:");
            e.printStackTrace();
            return;
        }
        if (legacy) {
            hook = new LegacyMcMMOHook();
        } else {
            hook = new McMMO2Hook();
        }
    }

    public boolean isTrue(BlockState state) {
        if (!isHookEnabled()) return false;
        return hook.isTrue(state);
    }

    public boolean isMcMMOPresent() {
        return mcMMOPresent;
    }

    public boolean isHookEnabled() {
        return hook != null;
    }
}
